select d.*, ftd.order_by
from films_to_directors ftd
  join director d on d.id=ftd.director_id
where ftd.film_id = ?
order by ftd.order_by
