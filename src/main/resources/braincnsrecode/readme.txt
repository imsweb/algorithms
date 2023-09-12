Data for the "SEER Brain/CNS Recode" algorithm.

2020 Revision
*************

https://seer.cancer.gov/seerstat/variables/seer/brain_cns-recode/

The downloadable Excel spreadsheet required a lot of manual manipulations to be usable in a program :-(

1. Some lines were "summary" lines, those will be ignored when the file is read.
2. I replaced all the Windows dashes with ASCII ones.
3. Some lines were combined, in which case the second line were missing some of the values, I manually re-added those.
4. I added leading C in front of all sites
5. Some lines contained hist exclusions instead of inclusion; I changed "Histology" to "Histology Inclusions" and added a new "Histology Exclusions" column
6. Some lines contained "anything not cover above"; I left those values blank, meaning any values. That means the order ini which the data rows are tried matters!


09/12/23 - I manually updated the histology ranges of the brain-cnsrecode-2020revision.csv file (changes were not posted online yet).