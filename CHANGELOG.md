## Algorithms Version History

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
