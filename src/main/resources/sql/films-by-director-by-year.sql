select f.*
from films_to_directors ftd
  join films f on f.id = ftd.film_id
where ftd .director_id = ?
order by EXTRACT(year FROM f.releasedate)
