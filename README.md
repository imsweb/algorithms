# SEER Algorithms

[![Build Status](https://travis-ci.org/imsweb/algorithms.svg?branch=master)](https://travis-ci.org/imsweb/algorithms)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.imsweb/algorithms/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.imsweb/algorithms)

This library contains the Java implementations of algorithms used in cancer-related data processing.

## Features

The following algorithms are available:
 
 * Death Classification ([CauseSpecificUtils](https://github.com/imsweb/algorithms/tree/master/src/main/java/com/imsweb/algorithms/causespecific/CauseSpecificUtils.java))
 * Census Tract Poverty Indicator ([CensusTractPovertyIndicatorUtils](https://github.com/imsweb/algorithms/tree/master/src/main/java/com/imsweb/algorithms/censustractpovertyindicator/CensusTractPovertyIndicatorUtils.java))
 * Historic Stage ([HistoricStageUtils](https://github.com/imsweb/algorithms/tree/master/src/main/java/com/imsweb/algorithms/historicstage/HistoricStageUtils.java))
 * ICCC Site Recode ([IcccRecodeUtils](https://github.com/imsweb/algorithms/tree/master/src/main/java/com/imsweb/algorithms/iccc/IcccRecodeUtils.java))
 * NAPIIA ([NapiiaUtils](https://github.com/imsweb/algorithms/tree/master/src/main/java/com/imsweb/algorithms/napiia/NapiiaUtils.java))
 * NHIA ([NhiaUtils](https://github.com/imsweb/algorithms/tree/master/src/main/java/com/imsweb/algorithms/nhia/NhiaUtils.java))
 * Rural Urban ([RuralUrbanUtils](https://github.com/imsweb/algorithms/tree/master/src/main/java/com/imsweb/algorithms/ruralurban/RuralUrbanUtils.java))
 * SEER Site Recode ([SeerSiteRecodeUtils](https://github.com/imsweb/algorithms/tree/master/src/main/java/com/imsweb/algorithms/seersiterecode/SeerSiteRecodeUtils.java))
 * Site-specific Surgery tables ([SiteSpecificSurgeryUtils](https://github.com/imsweb/algorithms/tree/master/src/main/java/com/imsweb/algorithms/surgery/SiteSpecificSurgeryUtils.java))
 * Survival Time ([SurvivalTimeUtils](https://github.com/imsweb/algorithms/tree/master/src/main/java/com/imsweb/algorithms/survival/SurvivalTimeUtils.java))
 * IARC Multiple Primary Algorithm ([IarcUtils](https://github.com/imsweb/algorithms/tree/master/src/main/java/com/imsweb/algorithms/iarc/IarcUtils.java))

## Download

The library is available on [Maven Central](http://search.maven.org/#search%7Cga%7C1%7Cg%3A%22com.imsweb%22%20AND%20a%3A%22algorithms%22).

To include it to your Maven or Gradle project, use the group ID `com.imsweb` and the artifact ID `algorithms`.

You can check out the [release page](https://github.com/imsweb/algorithms/releases) for a list of the releases and their changes.


## Algorithm versions

To know the version of a specific algorithm, click the link to the corresponding utility class and check the static variables defined on the top of the class.

Not all algorithms support a version.

This library does not support several versions of a given algorithm; it only contains the latest version available.

## Usage

### Input

Most algorithms allow two types of input:

1. A specific input Java object containing all the variables used in the algorithm.

2. A map of NAACCR items where the keys are the item names defined in the [layout framework](https://github.com/imsweb/layout).

The second method makes it very simple to process NAACCR data file; the layout framework can be used to read the record from the file and
those can then be provided as-is to the algorithms.

### Output

Most algorithms define a specific output Java object.

## About SEER

This library was developed through the [SEER](http://seer.cancer.gov/) program.

The Surveillance, Epidemiology and End Results program is a premier source for cancer statistics in the United States.
The SEER program collects information on incidence, prevalence and survival from specific geographic areas representing
a large portion of the US population and reports on all these data plus cancer mortality data for the entire country.