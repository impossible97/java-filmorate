insert into films_to_directors (director_id, film_id, order_by) values
    (?, ?, (select coalesce(max(order_by), 0) + 1 as new_order
            from films_to_directors ftd
            where film_id = ?))
