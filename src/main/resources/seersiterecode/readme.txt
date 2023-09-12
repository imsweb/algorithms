The data for the "SEER Site Recode" algorithm was taken from the SEER website:

https://seer.cancer.gov/siterecode/icdo3_dwhoheme/
https://seer.cancer.gov/siterecode/icdo3_d01272003/

The data was created by parsing the HTML from those pages.

**********************

The 2023 recode were taken from

https://seer.cancer.gov/siterecode/icdo3_2023/
https://seer.cancer.gov/siterecode/icdo3_2023_expanded/

Downloaded the Excel version and saved as CSV. Replaced the Windows "-" by ASCII dashes.

Ran a "SeerSiteRecodeParserLab" class to generate the data that can be consumed by the algorithm.


09/12/23 - I manually updated the histology ranges of the site-recode-data-2008.csv file (changes were not posted online yet).