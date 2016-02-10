## Algorithms Version History

**Changes in version 1.4.1**

 - Fixed a bug in surgery tables where all row levels would be null; introduced in version 1.4.
 - Added new variable to the output of the survival time calculation that contains the calculated order of the input record objects.
 - Improved some MPH rules.

**Changes in version 1.4**

 - Replaced JAXB by XStream for all internal XML operations.
 - This library now requires Java 7.

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
