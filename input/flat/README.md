What is flat?
====

Flat is a hyper-simple flat data format.  I use JSON for data serialization but it's cumbersome for big listy things.
Flat is not designed for heirarchtical data, JSON is still recommended for that.  Flat is for when you have items with flat
data.  Flat is designed to be readable (and writable!) so it has minimal syntax and minimal rules, you should be able to
"just know" how it works.

Basically a .flat file is a list.  Each item is a bunch of key-value pairs.  There are only 3 syntax symbols:

* Comma : denotates a list of values
* line-break (\n) : denotates end of key value pair
* double line-break (\n\n) : denotates end of item

```
name : object
color : red
counter :1
date: 12/12/1912
attrs:light, fast

name:object2
color:blue
shapes:circle,triangle
```

While values are strings they should be interpreted appropriately.  For example, 1/12/1991 should be a date, 123 should
be a number. Beginning and ending whitespace should be trimmed on the value and key.

To make arrays, use commas.  Blank or whitespace entries will be ignored.  You can use this behavior to make empty arrays
or arrays of one.

Array
```
name:object
shapes:circle,square
```

Array of one
```
name:object2
shapes:circle,
```

Array of none
```
name:object3
shapes:,
```

keys without a value will be undefined.

At present colons, commas and linebreaks cannot be present in keys (and likely will never be) or values (perhaps escaping
could be used).
