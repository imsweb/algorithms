# SEER Algorithms

[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.imsweb/algorithms/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.imsweb/algorithms)

This library contains the Java implementations of algorithms used in cancer-related data processing.

## Features

The following algorithms are available:

 * County at Diagnosis Analysis ([CountyAtDxAnalysisUtils](https://github.com/imsweb/algorithms/tree/master/src/main/java/com/imsweb/algorithms/countyatdiagnosisanalysis/CountyAtDxAnalysisUtils.java))
 * IARC Multiple Primary Algorithm ([IarcUtils](https://github.com/imsweb/algorithms/tree/master/src/main/java/com/imsweb/algorithms/iarc/IarcUtils.java))
 * ICD Conversions ([IcdUtils](https://github.com/imsweb/algorithms/tree/master/src/main/java/com/imsweb/algorithms/icd/IcdUtils.java))
 * International Classification of Childhood Cancer ([IcccRecodeUtils](https://github.com/imsweb/algorithms/tree/master/src/main/java/com/imsweb/algorithms/iccc/IcccRecodeUtils.java))
 * NAACCR Asian/Pacific Islander Identification Algorithm ([NapiiaUtils](https://github.com/imsweb/algorithms/tree/master/src/main/java/com/imsweb/algorithms/napiia/NapiiaUtils.java))
 * NAACCR Hispanic Identification Algorithm ([NhiaUtils](https://github.com/imsweb/algorithms/tree/master/src/main/java/com/imsweb/algorithms/nhia/NhiaUtils.java))
 * NAACCR Poverty Linkage Program ([CensusTractPovertyIndicatorUtils](https://github.com/imsweb/algorithms/tree/master/src/main/java/com/imsweb/algorithms/censustractpovertyindicator/CensusTractPovertyIndicatorUtils.java))
 * NAACCR Rural Urban Program (RUCA/URIC/Continuum) ([RuralUrbanUtils](https://github.com/imsweb/algorithms/tree/master/src/main/java/com/imsweb/algorithms/ruralurban/RuralUrbanUtils.java))
 * NAACCR Yost Quintile & Area-Based Social Measures Linkage Program ([YostAcsPovertyUtils](https://github.com/imsweb/algorithms/tree/master/src/main/java/com/imsweb/algorithms/yostacspoverty/YostAcsPovertyUtils.java))
 * NPCR PRCDA & UIHO Linkage Program ([PrcdaUihoUtils](https://github.com/imsweb/algorithms/tree/master/src/main/java/com/imsweb/algorithms/prcdauiho/PrcdaUihoUtils.java))
 * SEER AYA Site Recode ([AyaSiteRecodeUtils](https://github.com/imsweb/algorithms/tree/master/src/main/java/com/imsweb/algorithms/ayasiterecode/AyaSiteRecodeUtils.java))
 * SEER Behavior Recode ([BehaviorRecodeUtils](https://github.com/imsweb/algorithms/tree/master/src/main/java/com/imsweb/algorithms/behavrecode/BehaviorRecodeUtils.java))
 * SEER Cause-specific Death Classification ([CauseSpecificUtils](https://github.com/imsweb/algorithms/tree/master/src/main/java/com/imsweb/algorithms/causespecific/CauseSpecificUtils.java))
 * SEER Site Recode ([SeerSiteRecodeUtils](https://github.com/imsweb/algorithms/tree/master/src/main/java/com/imsweb/algorithms/seersiterecode/SeerSiteRecodeUtils.java))
 * Site-specific Surgery tables ([SiteSpecificSurgeryUtils](https://github.com/imsweb/algorithms/tree/master/src/main/java/com/imsweb/algorithms/surgery/SiteSpecificSurgeryUtils.java))
 * Survival Time in Months ([SurvivalTimeUtils](https://github.com/imsweb/algorithms/tree/master/src/main/java/com/imsweb/algorithms/survival/SurvivalTimeUtils.java))

## Download

The library is available on [Maven Central](http://search.maven.org/#search%7Cga%7C1%7Cg%3A%22com.imsweb%22%20AND%20a%3A%22algorithms%22).

To include it to your Maven or Gradle project, use the group ID `com.imsweb` and the artifact ID `algorithms`.

You can check out the [release page](https://github.com/imsweb/algorithms/releases) for a list of the releases and their changes.


## Algorithm versions

To know the version of a specific algorithm, click the link to the corresponding utility class and check the static variables defined on the top of the class.

Not all algorithms support a version.

This library does not support several versions of a given algorithm; it only contains the latest version available.

## Usage

There two ways to use this library: 

1. Every algorithm has a utility class that exposes one or several computation methods; those methods can be called directly.
2. Every algorithm also has an implementation of the Algorithm class which exposes a unified execute method.

The Algorithms class acts as a repository of all the algorithms and their input/output fields.

## About SEER

This library was developed through the [SEER](http://seer.cancer.gov/) program.

The Surveillance, Epidemiology and End Results program is a premier source for cancer statistics in the United States.
The SEER program collects information on incidence, prevalence and survival from specific geographic areas representing
a large portion of the US population and reports on all these data plus cancer mortality data for the entire country.