/**
 * This file is part of alf.io.
 *
 * alf.io is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * alf.io is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with alf.io.  If not, see <http://www.gnu.org/licenses/>.
 */
package alfio.repository;

import alfio.model.Event;
import alfio.model.PriceContainer;
import alfio.util.OptionalWrapper;
import ch.digitalfondue.npjt.*;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

@QueryRepository
public interface EventRepository {

    @Query("select * from event where id = :eventId")
    Event findById(@Bind("eventId") int eventId);
    
    @Query("select org_id from event where id = :eventId")
    int findOrganizationIdByEventId(@Bind("eventId") int eventId);

    @Query("select * from event where short_name = :eventName")
    Event findByShortName(@Bind("eventName") String eventName);

    @Query("select * from event where short_name = :eventName")
    Optional<Event> findOptionalByShortName(@Bind("eventName") String eventName);

    @Query("select * from event order by start_ts asc")
    List<Event> findAll();

    @Query("select * from event where org_id = :organizationId")
    List<Event> findByOrganizationId(@Bind("organizationId") int organizationId);

    @Query("insert into event(short_name, type, display_name, website_url, external_url, website_t_c_url, image_url, file_blob_id, location, latitude, longitude, start_ts, end_ts, time_zone, regular_price_cts, currency, available_seats, vat_included, vat, allowed_payment_proxies, private_key, org_id, locales, vat_status, src_price_cts, version, status) " +
            "values(:shortName, :type, :displayName, :websiteUrl, :externalUrl, :termsUrl, :imageUrl, :fileBlobId, :location, :latitude, :longitude, :start_ts, :end_ts, :time_zone, 0, :currency, :available_seats, :vat_included, :vat, :paymentProxies, :privateKey, :organizationId, :locales, :vatStatus, :srcPriceCts, :version, :status)")
    @AutoGeneratedKey("id")
    AffectedRowCountAndKey<Integer> insert(@Bind("shortName") String shortName, @Bind("type") Event.EventType type, @Bind("displayName") String displayName,
                                           @Bind("websiteUrl") String websiteUrl, @Bind("externalUrl") String externalUrl, @Bind("termsUrl") String termsUrl, @Bind("imageUrl") String imageUrl, @Bind("fileBlobId") String fileBlobId,
                                           @Bind("location") String location, @Bind("latitude") String latitude, @Bind("longitude") String longitude, @Bind("start_ts") ZonedDateTime begin,
                                           @Bind("end_ts") ZonedDateTime end, @Bind("time_zone") String timeZone, @Bind("currency") String currency,
                                           @Bind("available_seats") int available_seats, @Bind("vat_included") boolean vat_included,
                                           @Bind("vat") BigDecimal vat, @Bind("paymentProxies") String allowedPaymentProxies,
                                           @Bind("privateKey") String privateKey,
                                           @Bind("organizationId") int orgId,
                                           @Bind("locales") int locales,
                                           @Bind("vatStatus") PriceContainer.VatStatus vatStatus,
                                           @Bind("srcPriceCts") int srcPriceCts,
                                           @Bind("version") String version,
                                           @Bind("status") Event.Status status);

    @Query("update event set status = :status where id = :id")
    int updateEventStatus(@Bind("id") int id, @Bind("status") Event.Status status);

    @Query("update event set display_name = :displayName, website_url = :websiteUrl, external_url = :externalUrl, website_t_c_url = :termsUrl, image_url = :imageUrl, file_blob_id = :fileBlobId, " +
            "location = :location, latitude = :latitude, longitude = :longitude, start_ts = :start_ts, " +
            "end_ts = :end_ts, time_zone = :time_zone, org_id = :organizationId, locales = :locales where id = :id")
    int updateHeader(@Bind("id") int id, @Bind("displayName") String displayName, @Bind("websiteUrl") String websiteUrl, @Bind("externalUrl") String externalUrl, @Bind("termsUrl") String termsUrl, @Bind("imageUrl") String imageUrl, @Bind("fileBlobId") String fileBlobId,
                     @Bind("location") String location, @Bind("latitude") String latitude, @Bind("longitude") String longitude, @Bind("start_ts") ZonedDateTime begin,
                     @Bind("end_ts") ZonedDateTime end, @Bind("time_zone") String timeZone, @Bind("organizationId") int organizationId, @Bind("locales") int locales);

    @Query("update event set currency = :currency, available_seats = :available_seats, vat_included = :vat_included, vat = :vat, allowed_payment_proxies = :paymentProxies, vat_status = :vatStatus, src_price_cts = :srcPriceCts where id = :eventId")
    int updatePrices(@Bind("currency") String currency,
                     @Bind("available_seats") int available_seats, @Bind("vat_included") boolean vat_included,
                     @Bind("vat") BigDecimal vat, @Bind("paymentProxies") String allowedPaymentProxies, @Bind("eventId") int eventId,
                     @Bind("vatStatus") PriceContainer.VatStatus vatStatus,
                     @Bind("srcPriceCts") int srcPriceCts);

    @Query("select a.* from event a, ticket b where b.tickets_reservation_id = :reservationId and b.event_id = a.id limit 1")
    Event findByTicketReservationId(@Bind("reservationId") String reservationId);

    @Query("select a.* from event a, additional_service_item b where b.tickets_reservation_uuid = :reservationId and b.event_id_fk = a.id limit 1")
    Event findByAdditionalServiceReservationId(@Bind("reservationId") String reservationId);

    default Event findByReservationId(String reservationId) {
        return OptionalWrapper.optionally(() -> findByTicketReservationId(reservationId)).orElseGet(() -> findByAdditionalServiceReservationId(reservationId));
    }

    @Query("update event set display_name = short_name where id = :eventId and display_name is null")
    int fillDisplayNameIfRequired(@Bind("eventId") int eventId);

    @Query("select count(*) from event where short_name = :shortName")
    Integer countByShortName(@Bind("shortName") String shortName);

    @Query("select id from event where end_ts > :now")
    List<Integer> findAllActiveIds(@Bind("now") ZonedDateTime now);

    @Query("select available_seats from event where id = :id")
    Integer getAvailableSeats(@Bind("id") int eventId);

    @Query("update event set available_seats = :newValue where id = :eventId")
    int updateAvailableSeats(@Bind("eventId") int eventId, @Bind("newValue") int newValue);

    @Query("select * from event where short_name = :name for update")
    Optional<Event> findOptionalByShortNameForUpdate(@Bind("name") String shortName);
}
