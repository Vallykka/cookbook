-- :name dishes-by-name :? :*
-- :doc Get list of dishes by name
select id, name from dish where lower(name) like lower(:name)

-- :name dish-by-id :? :1
-- :doc Get selected dish
select name, algo, ingredients from dish where id = :id

-- :name product :*
-- :doc Get product list
select * from product

-- :name measure :*
-- :doc Get measure list
select * from measure

-- :name insert-dish :<!
-- :doc Inserts dish
insert into dish (name, algo, ingredients) values (:name, :algo, :ingredients) returning id

-- :name update-dish :<!
-- :doc Updates dish
update dish set name = :name, algo = :algo, ingredients = :ingredients where id = :id returning id

-- :name delete-dish :<! :1
-- :doc Deletes dish
delete from dish where id = :id