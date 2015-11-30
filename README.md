# Algorithms

[![Build Status](https://travis-ci.org/imsweb/algorithms.svg?branch=master)](https://travis-ci.org/imsweb/algorithms)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.imsweb/algorithms/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.imsweb/algorithms)

This library contains the Java implementations of algorithms used in cancer-related data processing.

## Features

The following algorithms are available:
 
 * Death Classification ([CauseSpecificUtils](https://github.com/imsweb/algorithms/tree/master/src/main/java/com/imsweb/algorithms/causespecific/CauseSpecificUtils))
 * Census Tract Poverty Indicator ([CensusTractPovertyIndicatorUtils](https://github.com/imsweb/algorithms/tree/master/src/main/java/com/imsweb/algorithms/censustractpovertyindicator/CensusTractPovertyIndicatorUtils))
 * Historic Stage ([HistoricStageUtils](https://github.com/imsweb/algorithms/tree/master/src/main/java/com/imsweb/algorithms/historicstage/HistoricStageUtils))
 * ICCC Site Recode ([IcccRecodeUtils](https://github.com/imsweb/algorithms/tree/master/src/main/java/com/imsweb/algorithms/iccc/IcccRecodeUtils))
 * MPH rules ([MPUtils](https://github.com/imsweb/algorithms/tree/master/src/main/java/com/imsweb/algorithms/multipleprimary/MPUtils))
 * NAPIIA ([NapiiaUtils](https://github.com/imsweb/algorithms/tree/master/src/main/java/com/imsweb/algorithms/napiia/NapiiaUtils))
 * NHIA ([NhiaUtils](https://github.com/imsweb/algorithms/tree/master/src/main/java/com/imsweb/algorithms/nhia/NhiaUtils))
 * Rural Urban ([RuralUrbanUtils](https://github.com/imsweb/algorithms/tree/master/src/main/java/com/imsweb/algorithms/ruralurban/RuralUrbanUtils))
 * SEER Site Recode ([SeerSiteRecodeUtils](https://github.com/imsweb/algorithms/tree/master/src/main/java/com/imsweb/algorithms/seersiterecode/SeerSiteRecodeUtils))
 * Site-specific Surgery tables ([SiteSpecificSurgeryUtils](https://github.com/imsweb/algorithms/tree/master/src/main/java/com/imsweb/algorithms/surgery/SiteSpecificSurgeryUtils))
 * Survival Time ([SurvivalTimeUtils](https://github.com/imsweb/algorithms/tree/master/src/main/java/com/imsweb/algorithms/survival/SurvivalTimeUtils))

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