delete from films_to_directors where film_id = ? and director_id = coalesce(?, director_id)