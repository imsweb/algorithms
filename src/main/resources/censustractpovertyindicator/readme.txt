The original data for the poverty indicator algorithms came from the American factfinder site on the Census website.
That old site is no loner available; the new Census website is now https://data.census.gov.

For example, here is a link to table S1701 for 2013-17 data: https://data.census.gov/cedsci/table?q=s1701&tid=ACSST5Y2018.S1701

Select counties by clicking on the customize table button and adding a "Geo" filter. Because of the size of the table it's not possible to display
the table for all counties, but it's possible to download the data.

Note that the website provides 5-years estimates (it also provides 1-year estimates, but the library uses the 5-years). That whey most of the CSV
data files have a range in their name: the file used for DX year 2015 is the "2013-2017" file, which contains the 5-years average across 2013-2017.

The download files contain a table with a lot of columns, but the first column with a percentage is the one the library uses.
From that point the percentage needs to be converted to the poverty indicator value. This can be done in Excel with an if statement.
The final values for the indicator are:

* 1 = less than 5%
* 2 = greater or equal to 5%, but less than 10%
* 3 = greater or equal to 10%, but less than 20%
* 4 = greater or equal to 20%
* 9 = Unknown