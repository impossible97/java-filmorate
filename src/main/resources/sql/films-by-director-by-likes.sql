select f.*
from (
  select /* a */
    count(l.user_id) over (partition by ftd.film_id, ftd.director_id ) as like_cnt,
    ftd.film_id,
    ftd.director_id
  from
      films_to_directors ftd
      left join likes l on ftd.film_id = l.film_id
  ) a
  join films f on f.id = a.film_id
where a.director_id = ?
group by id, name, description, releasedate, duration, rating_id, a.like_cnt
order by a.like_cnt desc
