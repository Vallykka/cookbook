drop type if exists product_type;
--;;
create type product_type as enum ('VEGETABLE', 'MEAT', 'FISH', 'EGG', 'FRUIT', 'MILK', 'SPICE', 'GREASE', 'OIL', 'OTHER');