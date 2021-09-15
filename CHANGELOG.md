## Algorithms Version History

**Changes in version 3.7**

- Fixed ICCC algorithm Third Edition/IARC 2017 that generated wrong output.
- Updated XStream library to version 1.4.18; it had previously been updated to version 1.4.8 by mistake.
- Updated CSV library from version 5.5.1 to version 5.5.2.

**Changes in version 3.5**

- Updated PRCDA and UIHO algorithms with 2020 data tables.
- Added new algorithm EPHT SubCounty.
- Added new algorithm to calculate Tract Estimated Congressional Districts.
- Added new algorithm to calculate Cancer Reporting Zones.
- Updated NAACCR XML library from version 1.4.17 to version 1.4.18.
- Updated Apache commons-lang library from version 3.11 to version 3.12.0.
- Updated CSV library from version 5.4 to version 5.5.1.

**Changes in version 3.4**

- Changed PRCDA and UIHO algorithms to use County at DX Analysis instead of reported County at DX.
- Updated NAACCR XML library from version 1.4.10 to version 1.4.17.
- Updated CSV library from version 5.2 to version 5.4.
- Updated Apache commons-lang library from version 3.9 to version 3.11.

**Changes in version 3.3**

- Added site-specific surgery tables for 2021 DX year.
- Fixed PRCDA and UIHO algorithms that were ignoring first line of their internal CSV data file.

**Changes in version 3.2**

- Changed Behavior recode logic for 9421-9422 histology codes.

**Changes in version 3.1**

- Added new version of the ICCC algorithm and switched the default algorithm to that version.
- Added new utility class to return schema-specific lookup for neoadjuvTherapyTreatmentEffect (#1634) field.

**Changes in version 3.0**

- Changed NHIA and NAPIIA algorithms to use the new birth surname field, the old maiden name field is still used if the new one is blank.
- Fixed IARC algorithms that sometimes returned the full histology code instead of the computed histology group; changed site group field to be 3 characters long instead of 4.
- Removed all deprecated methods.
- Updated CSV library from version 5.1 to version 5.2.

**Changes in version 2.18**

- The UIHO lookup table was revised.

**Changes in version 2.17**

- This version was never released.

**Changes in version 2.16**

- Fixed ICCC field name being more than 50 characters, making it invalid for NAACCR XML dictionaries.
- Added new algorithm for SEER Adolescents and Young Adults (AYA) Site Recode.

**Changes in version 2.15**

- Added an algorithm to retrieve Yost quintiles and area-based social measures for a state/county/tract.
- Fixed an issue with NHIA where individuals who were born in predominantly non-Hispanic countries were being wrongly set to non-Hispanic.
- Renamed output variable names for PRCDA and UIHO algorithms to match official NAACCR XML IDs.
- The lookup tables used by the PRCDA and UIHO algorithms were updated with the latest information.
- Census revised the 2010 RUCA lookup table to correct a programming error affecting the secondary RUCA codes. The revision corrected the secondary RUCA codes of 10,909 census tracts. The revised secondary codes will result in an increase in the number of census tracts classified as rural. Our lookup table was revised to be in line with Census.
- Updated the census tract poverty indicator algorithm by adding a lookup table for 2014-2018 data and modifying the logic to take the new table into account.

**Changes in version 2.14**

- Now computing death classification for sequences 2-59.
- Fixed small bug in Historic Stage definition for Pancreas Head & Tail lymph nodes
- Updated CSV library from version 5.0 to version 5.1.

**Changes in version 2.13**

- Updated Cause-Specific Survival algorithm to fix error in handling of anal cancers.

**Changes in version 2.12**

- Restructured internal algorithms.
- Updated CSV library from version 4.2 to version 5.0.
- Updated Apache commons-lang library from version 3.7 to version 3.9.

**Changes in version 2.11**

- Changed a few algorithm names to make them a bit shorter.
- Added more information for the algorithms and their input/output fields.

**Changes in version 2.10**

- Fixed County at DX algorithm setting the wrong input field.

**Changes in version 2.9**

- Removed unused input fields from County at DX Analysis algorithm. 
- Fixed IARC algorithm crashing when input sequence number is blank.

**Changes in version 2.8**

- Updated the PRCDA/UIHO algorithm.  There were some changes to when "Unknown" was used.
- Renamed the algorithm API fields associated with the PRCDA/UIHO algorithm.

**Changes in version 2.7**

- Fixed a mistake in the selection of the Hematopoietic site-specific surgery table.
- Now zero-padding the record number recode in the survival algorithms; this only affects the new Algorithms framework.

**Changes in version 2.6**

- Fixed some mistakes in the selection of the site-specific surgery tables.

**Changes in version 2.5**

- Corrected an issue with the 2012-2016 Census Tract Poverty Indicator lookup table/resource file.

**Changes in version 2.4**

- Updated the Census Tract Poverty Indicator algorithm to handle 2015+ cases using the latest 5 year data from the ACS (2013-2017).
- Added an algorithm to look up Purchased/Referred Care Delivery Areas (PRCDA), Urban Indian Health Organizations (UIHO), and UIHO Facility Numbers for a given state/county.
- County at Diagnosis Reported (#90) was replaced with County at Diagnosis for Analysis (#89) in the following algorithms: NHIA, RUCA, URIC, Urban/Rural Continuum, Census Tract Poverty. 

**Changes in version 2.3**

- Improved the sorting mechanism of Survival Time Algorithm to handle corner cases.

**Changes in version 2.2**

- Fixed a few site-specific surgery tables that slightly changed for the 2018 DX year.

**Changes in version 2.1**

- Added new algorithm to compute County at DX Analysis.
- Added computed field icccMajorCategory to the ICCC algorithm.
- Added site-specific surgery tables for years 2003-2009.

**Changes in version 2.0**

- Changed Survival Time Algorithm to calculate record number recode for all records even if the record is considered invalid.
- Changed internal initialization of NHIA and NAPIIA algorithms to make them thread-safe.
- Changed default value of NAPIIA from an empty string to null.
- Added missing code SVK (for Slovakia) to the non-hispanic list based on birth place for NHIA.
- Deprecated some methods using layout properties.
- Added new framework to standardize the algorithms and allow them to be registered and executed via a factory.

**Changes in version 1.21**

- Site-specific surgery tables are now year-specific.

**Changes in version 1.20**

- Fixed synchronization issues in the initialization of the RUCA/URIC and Poverty Indicator algorithms.

**Changes in version 1.19**

- Fixed warnings in the console about unsafe access to private fields.

**Changes in version 1.18**

- Changed Survival Time algorithm so it returns null values for no-value year/month/day instead of spaces.
- Updated the SEER Historic Stage algorithm to reflect the algorithm used for the current SEER research data.
- Updated NAACCR XML library from version 1.4.10 to version 1.4.11.1

**Changes in version 1.17**

- Changed computation of RUCA/URIC variables in RuralUrbanUtils; renamed some methods.
- Added support to IARC Multiple Primary Algorithm.
- Changed Cause Specific algorithm to support missing or unknown cause of death values.
- Updated the census tract poverty linkage algorithm for 2018.

**Changes in version 1.16**

- Updated Apache commons-lang library from version 3.6 to version 3.7.
- Updated CSV library from version 2.4 to version 4.2.

**Changes in version 1.15**

- Updated Survival Time algorithm to calculate vital status recode.
- Added several values to ICD-10 to ICD-O-3 conversions spreadsheet.
- Restructured the rural/urban CSV data providers.

**Changes in version 1.14**

- Removed ACS Linkage algorithm.

**Changes in version 1.13**

- Changed Death Classification algorithm to allow 0 or 4 as the value "dead" for vital status.
- Added support for ICD-O-3 to ICD-O-2 conversion in IcdUtils class; renamed a few methods and classes in that module.

**Changes in version 1.12**

- Fixed a mistake in ICD-10-CM to ICD-O-3 conversion for code D044.

**Changes in version 1.11**

- Changed survival algorithm so the record recode is 1-based instead of 0-based.

**Changes in version 1.10**

- Added a proper security environment to XStream by limiting the classes that it can create when loading XML files.
- Updated XStream library from version 1.4.9 to 1.4.10.
- Updated Apache commons-lang library from version 3.4 to version 3.6.

**Changes in version 1.9.1**

- Fixed minor issue in ACS Linkage data.

**Changes in version 1.9**

- Added new algorithm for ACS Linkage.

**Changes in version 1.8**

- Updated list of heavily and rarely Hispanic names for NHIA algorithm; updated the version for both NHIA and NAPIIA (since they go together).
- Added new census tract poverty indicator calculation for 2011-2015 years.

**Changes in version 1.7**

- Added support for ICD-9CM, ICD-10CM and ICD-10 conversions to ICD-O-3 (see IcdUtils); conversions are based on spreadsheet provided by NCI.
- Change internal implementation of census tract poverty indicator, should be no change in the behavior of the algorithm.
- Fixed bad code range in ICCC third edition data; group 106 was missing a "C" in its range.

**Changes in version 1.6.2**

- Exposed methods isHeavilyHispanic() and isRarelyHispanic() in NhiaUtils.
 
**Changes in version 1.6.1**

- Change census tract poverty indicator version to 6.0, released in October 2016.

**Changes in version 1.6**

- Changed calculated census poverty indicator for recent years.

**Changes in version 1.5**

- Removed Joda library dependency, replaced by new Java 8 date framework.
- Updated Commons Lang library from version 3.3.2 to version 3.4.
- Updated XStream library from version 1.4.7 to version 1.4.9.
- This library now requires Java 8 at minimum.

**Changes in version 1.4.4**

- Removed the MPH module; it has been moved into its own project (https://github.com/imsweb/mph)

**Changes in version 1.4.3**

- Changed the parser used by XStreams to read XML files, was causing some fields in the site-specific surgery XML files to be ignored.

**Changes in version 1.4.2**

- Added support for calculating the extended ICCC recode.
- Added support for calculating SEER behavior recode for analysis.

**Changes in version 1.4.1**

- Fixed a bug in surgery tables where all row levels would be null; introduced in version 1.4.
- Added new variable to the output of the survival time calculation that contains the calculated order of the input record objects.
- Improved some MPH rules.

**Changes in version 1.4**

- Replaced JAXB by XStream for all internal XML operations.
- This library now requires Java 7 at minimum.

**Changes in version 1.3.5**

- Changed places XCZ and ZYG to CSK and YUG in NHIA algorithm, per NAACCR 15 changes; updated NHAPIIA version to v15.

**Changes in version 1.3.4**

- Updated CS Extension, Node and Mets lookups in Historic Stage module.

**Changes in version 1.3.3**

- MPH algorithm now returns NON_APPLICABLE if any of the incoming tumors has a DX year < 2007 (or unknown).

**Changes in version 1.3.2**

- Fixed an issue in one of the lung MPH rules.

**Changes in version 1.3.1**

- Fixed a bad code (4 characters instead of 3) in new ICCC data.

**Changes in version 1.3**

- Added support in Rural Urban utility class to expose the rural urban census percentages; they are now part of the output object.
- Updated the 2000 data for Rural Urban variables.

**Changes in version 1.2.1**

- Fixed the WHO 2008 ICCC recode data, was using the extended classification instead of the main one.

**Changes in version 1.2**

- Fixed a bug in MPH related to paired sites.
- Added support for WHO 2008 ICCC site recoding.

**Changes in version 1.1**

- Updated commons-lang library from 2.x to 3.x.

**Changes in version 1.0**

- Module split from SEER*Utils into its own project.

**Legacy changes**

- [SEER*Utils v4.9  ]  Moved all algorithm classes from "com.imsweb.seerutils.tools" to individual packages.
- [SEER*Utils v4.9  ]  Fixed an issue with survival time related to future dates.
- [SEER*Utils v4.8.6]  Fixed an exception happening in the Survival Time algorithm for specific time zones.
- [SEER*Utils v4.8.5]  Tweaked some data for Rural Urban variables.
- [SEER*Utils v4.8.5]  Updated version of the algorithm to 2.2; now setting all output to unknown/blank when any of the DOLC or VS aren't the same among all the incoming records.
- [SEER*Utils v4.8.4]  Updated Site-Specific Surgery tables as they are defined in the newly released 2014 SEER manuals.
- [SEER*Utils v4.8.4]  Made a slight modification to the Survival Time calculated variables; the resulting dates will be set to blank when some inputs are invalid.
- [SEER*Utils v4.8.4]  Added support for calculated Rural Urban variables.
- [SEER*Utils v4.8.2]  Added a method in NhiaUtils to get the list of counties with low Hispanic ethnicity, for every state; this is useful since it's used by the algorithm to determine whether the special options can be used or not.
- [SEER*Utils v4.8  ]  Added support for Cause-Specific Death Classification.
- [SEER*Utils v4.6.3]  Fixed a threading issue with Historic Stage initialization.
- [SEER*Utils v4.6.2]  Now using 9 for future/unknown years in census tract indicator calculation; changed default behavior to include recent years in the calculation.
- [SEER*Utils v4.6.1]  Fixed a bug in the census tract poverty indicator calculation that would result in a bad calculated value in some rare cases.
- [SEER*Utils v4.6  ]  Fixed a NullPointerException in HistoricStageUtils.
- [SEER*Utils v4.5.6]  Added support for calculating Census Tract Poverty Indicator.
- [SEER*Utils v4.5.5]  Added more ways of calling the NHIA and NAPIIA algorithms.
- [SEER*Utils v4.5.5]  Fixed some corner cases with the Survival Time calculation.
- [SEER*Utils v4.5.5]  Added version and name information as constants in the Survival Time utility class.
- [SEER*Utils v4.5.5]  Updated NHAPIIA to version 13 released in October 2013.
- [SEER*Utils v4.5.3]  Modified the survival time calculation according to the new requirements released on the SEER website.
- [SEER*Utils v4.5  ]  Added a method in the SEER recode utility class to return the name for a given recode.
- [SEER*Utils v4.5  ]  Modifications to survival time calculation
- [SEER*Utils v4.5  ]  Added utility class for calculating Historic Stage variable.
- [SEER*Utils v4.4.1]  Updated the site-specific surgery tables.
- [SEER*Utils v4.4  ]  Added NAPIIA calculation.
- [SEER*Utils v4.4  ]  Added NHIA calculation.
- [SEER*Utils v4.1.2]  Set Surgery table versions to a more meaningful value
- [SEER*Utils v4.0.4]  Add new survival calculation algorithm
- [SEER*Utils v4.0.3]  Add new 2010+ SEER Site Recodes
- [SEER*Utils v4.0  ]  Added site-specific surgery tables.
- [SEER*Utils v2.1  ]  Add ICCC Site Recode
- [SEER*Utils v1.3  ]  Add a method in MathUtils to indicate whether the regression was an average
