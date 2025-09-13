package com.accounting.service.notification;

import com.accounting.entity.Client;
import com.accounting.entity.Notification;
import com.accounting.entity.Offer;
import com.accounting.entity.Reservation;
import com.accounting.entity.enums.ContextType;
import com.accounting.entity.enums.NotificationCategory;
import com.accounting.entity.enums.NotificationType;
import com.accounting.repository.NotificationRepository;
import com.accounting.repository.OfferRepository;
import com.accounting.repository.ReservationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationSchedulerService {

    private final ReservationRepository reservationRepository;
    private final NotificationService notificationService;
    private final OfferRepository offerRepository;
    private final NotificationRepository notificationRepository;

    /* Notification logic:
     * -> each notification type is signaled once per offer, per client:
     *     -> each reservation can have multiple notification types (return date, departure date, payment due, etc.).
     *          -> for each type, we check if a notification of that specific type already exists for that reservation.
     *               -> if it doesn’t exist, creates it.
     *               -> if it does exist, skips it.
     *
     *  -> for every active reservation
     *      -> if res.return_date is past 2 day -> create notification
     *      -> if res.departure_date is in 1 day -> create notification
     *      -> if res.payment_due_date is in 2 day -> create notification
     *
     *  -> for every active offer
     *      -> if offer create_date is older than 2 days (of today)
     *           -> create notification (nr de tel si numele clientului)
     *
     * -> for all ACTIVE notifications, if older than 2 days, then mark as ARCHIVED
     *
     */

    /**
     * Runs every day at 02:00 AM server time.
     */
    @Scheduled(cron = "0 0 8 * * *")
    public void generateNotifications() {
        LocalDate now = LocalDate.now();

        generateReturnDateNotifications(now);
        generateDepartureDateNotifications(now);
        generatePaymentDueDateNotifications(now);
        generateOfferNotifications(now);
        markActiveNotificationToArchive(now);
    }

    private void markActiveNotificationToArchive(LocalDate now) {
        Date daysOlder2 = Date.from(now.minusDays(2).atStartOfDay(ZoneId.systemDefault()).toInstant());
        List<Notification> updatedNotificationList = new ArrayList<>();

        notificationRepository.findNotificationToArchive(daysOlder2, NotificationCategory.ACTIVE)
                .forEach(notification -> {
                    notification.setNotificationCategory(NotificationCategory.ARCHIVED);
                    updatedNotificationList.add(notification);
                });

        if (!updatedNotificationList.isEmpty()) {
            notificationRepository.saveAll(updatedNotificationList);
        }
    }

    private void generateReturnDateNotifications(LocalDate now) {
        Date daysOlder2 = Date.from(now.minusDays(2).atStartOfDay(ZoneId.systemDefault()).toInstant());
        List<Reservation> reservations = reservationRepository.findReservationsWithReturnDate(daysOlder2);

        reservations.forEach(r -> {
            Client client = r.getClient();

            boolean exists = notificationRepository.existsByContextTypeAndContextIdAndNotificationType(
                    ContextType.RESERVATION,
                    r.getReservationId(),
                    NotificationType.RESERVATION_RETURN
            );

            // notify only once per offer
            if (!exists) {
                notificationService.createNotification(client,
                        String.format("Clientul %s s-a intors de 2 zile din vacanta.", client.getFullName()),
                        ContextType.RESERVATION, r.getReservationId(), NotificationType.RESERVATION_RETURN);
            }
        });
    }

    private void generateDepartureDateNotifications(LocalDate now) {
        Date in1Day = Date.from(now.plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant());
        List<Reservation> reservations = reservationRepository.findReservationsWithDepartureDate(in1Day);

        reservations.forEach(r -> {
            Client client = r.getClient();

            boolean exists = notificationRepository.existsByContextTypeAndContextIdAndNotificationType(
                    ContextType.RESERVATION,
                    r.getReservationId(),
                    NotificationType.RESERVATION_DEPARTURE
            );

            // notify only once per offer
            if (!exists) {
                notificationService.createNotification(client,
                        String.format("Clientul %s pleaca maine in vacanta.", client.getFullName()),
                        ContextType.RESERVATION, r.getReservationId(), NotificationType.RESERVATION_DEPARTURE);
            }
        });
    }

    private void generatePaymentDueDateNotifications(LocalDate now) {
        Date in2Days = Date.from(now.plusDays(2).atStartOfDay(ZoneId.systemDefault()).toInstant());
        List<Reservation> reservations = reservationRepository.findReservationsWithPaymentDueDate(in2Days);

        reservations.forEach(r -> {
            Client client = r.getClient();

            boolean exists = notificationRepository.existsByContextTypeAndContextIdAndNotificationType(
                    ContextType.RESERVATION,
                    r.getReservationId(),
                    NotificationType.RESERVATION_PAYMENT
            );

            // notify only once per offer
            if (!exists) {
                notificationService.createNotification(client,
                        String.format("Ziua de plata este in 2 zile pentru clientul %s.", client.getFullName()),
                        ContextType.RESERVATION, r.getReservationId(), NotificationType.RESERVATION_PAYMENT);
            }
        });
    }

    private void generateOfferNotifications(LocalDate now) {
        Date daysOlder2 = Date.from(now.minusDays(2).atStartOfDay(ZoneId.systemDefault()).toInstant());
        List<Offer> offers = offerRepository.findOffersOlderThan(daysOlder2);

        offers.forEach(offer -> {
            String message = String.format(
                    "Offer %d for client %s (%s) is older than 2 days",
                    offer.getOfferId(),
                    offer.getClient().getName(),
                    offer.getClient().getTelephone()
            );

            boolean exists = notificationRepository.existsByContextTypeAndContextIdAndNotificationType(
                    ContextType.OFFER,
                    offer.getOfferId(),
                    NotificationType.OFFER
            );

            // notify only once per offer
            if (!exists) {
                notificationService.createNotification(
                        offer.getClient(),
                        message,
                        ContextType.OFFER,
                        offer.getOfferId(),
                        NotificationType.OFFER);
            }
        });
    }

}
