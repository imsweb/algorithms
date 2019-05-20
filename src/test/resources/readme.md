Auto-registered algorithms feature

NHIA
option 0, 7 or 9 (no default, it has to be provided)
patient-level algorithm, but it does use 2 DX fields; can run on a single tumor using parent fields, or computes the best DX if patient is provided...
input fields:
- spanishHispanicOrigin (Patient) [spanishHispanicOrigin/190]
- nameLast (Patient) [nameLast/2230]
- nameMaiden (Patient) [nameMaiden/2390]
- birthplaceCountry (Patient) [birthplaceCountry/254]
- race1 (Patient) [race1/160]
- sex (Patient) [sex/220]
- ihs (Patient) [ihsLink/192]
- countyAtDx (Tumor) [countyAtDx/90]
- stateAtDx (Tumor) [addrAtDxState/80]
output fields:
- nhia (Patient) [nhiaDerivedHispOrigin/191]

NAPIIA
no option
input fields:
- race1 (Patient) [race1/160]
- race2 (Patient) [race2/161]
- race3 (Patient) [race3/162]
- race4 (Patient) [race4/163]
- race5 (Patient) [race5/164]
- spanishHispanicOrigin (Patient) [spanishHispanicOrigin/190]
- birthplaceCountry (Patient) [birthplaceCountry/254]
- sex (Patient) [sex/220]
- nameLast (Patient) [nameLast/2230]
- nameMaiden (Patient) [nameMaiden/2390]
- nameFirst (Patient) [nameFirst/2240]
output fields:
- napiiaValue (Patient) [raceNapiia/193]
- needsHumanReview (Patient, Non standard) - use napiiaNeedsHumanReview
- reasonForReview (Patient, Non standard) - napiiaReasonForReview

Death Classification
option cutoffYear
2 computations (2 fields, 1 alg)
input fields:
- primarySite (Tumor) [primarySite/400]
- histologyIcdO3 (Tumor) [histologicTypeIcdO3/522]
- sequenceNumberCentral (Tumor) [sequenceNumberCentral/380]
- icdRevisionNumber (Patient) [icdRevisionNumber/1920]
- causeOfDeath (Patient) [causeOfDeath/1910]
- dateOfLastContactYear (Patient) [dateOfLastContact/1750]
output fields:
- causeSpecificDeathClassification (Tumor) [seerCauseSpecificCod/1914] // bug in N18 dictionary, these are Tumor and not Patient!
- causeOtherDeathClassification (Tumor) [seerOtherCod/1915] // bug in N18 dictionary, these are Tumor and not Patient!

Census Tract Poverty
option includeRecentYears (defaults to true)
input fields:
- addressAtDxState (Tumor) [addrAtDxState/80]
- addressAtDxCounty (Tumor) [countyAtDx/90]
- dateOfDiagnosisYear (Tumor) [dateOfDiagnosis/390]
- censusTract2000 (Tumor) [censusTract2000/130]
- censusTract2010 (Tumor) [censusTract2010/135]
output fields:
- censusTractPovertyIndicator (Tumor) [censusTrPovertyIndictr/145]

Rural/Urban
no option
3 computations; I think this one has to be 3 algorithms because of the lazy initialization of the data
input fields:
- addressAtDxCounty (Tumor) [countyAtDx/90]
- addressAtDxState (Tumor) [addrAtDxState/80]
- censusTract2000 (Tumor) [censusTract2000/130]
- censusTract2010 (Tumor) [censusTract2010/135]
output fields:
- urbanRuralIndicatorCode2000 (Tumor) [uric2000/345]
- urbanRuralIndicatorCode2010 (Tumor) [uric2010/346]
- urbanRuralIndicatorCode2000Percentage (Tumor, Non standard) - use uric2000Percentage
- urbanRuralIndicatorCode2010Percentage (Tumor, Non standard) - use uric2010Percentage
- ruralUrbanCommutingArea2000 (Tumor) [ruca2000/339]
- ruralUrbanCommutingArea2010 (Tumor) [ruca2010/341]
- ruralUrbanContinuum1993 (Tumor) [ruralurbanContinuum1993/3300]
- ruralUrbanContinuum2003 (Tumor) [ruralurbanContinuum2003/3310]
- ruralUrbanContinuum2013 (Tumor) [ruralurbanContinuum2013/3312]

Survival Time
option endPointYear (also called study-cutoff); no default, it has to be provided
this algorithm is a patient-level algorithm but it needs all tumors
input fields:
- patientIdNumber (Patient) [patientIdNumber/20]
- dateOfDiagnosisYear (Tumor) [dateOfDiagnosis/390]
- dateOfDiagnosisMonth (Tumor) [dateOfDiagnosis/390]
- dateOfDiagnosisDay (Tumor) [dateOfDiagnosis/390]
- dateOfLastContactYear (Patient) [dateOfLastContact/1750]
- dateOfLastContactMonth (Patient) [dateOfLastContact/1750]
- dateOfLastContactDay (Patient) [dateOfLastContact/1750]
- birthYear (Patient) [dateOfBirth/240]
- birthMonth (Patient) [dateOfBirth/240]
- birthDay (Patient) [dateOfBirth/240]
- vitalStatus (Patient) [vitalStatus/1760]
- sequenceNumberCentral (Tumor) (Tumor) [sequenceNumberCentral/380]
- typeOfReportingSource (Tumor) [typeOfReportingSource/500]
output fields:
- vitalStatusRecode (Patient) [vitalStatusRecode/1762]
- survivalTimeDxYear (Tumor) [survDateDxRecode/1788]
- survivalTimeDxMonth (Tumor) [survDateDxRecode/1788]
- survivalTimeDxDay (Tumor) [survDateDxRecode/1788]
- survivalTimeDolcYear (Tumor) [survDateActiveFollowup/1782]
- survivalTimeDolcMonth (Tumor) [survDateActiveFollowup/1782]
- survivalTimeDolcDay (Tumor) [survDateActiveFollowup/1782]
- survivalTimeDolcYearPresumedAlive (Tumor) [survDatePresumedAlive/1785]
- survivalTimeDolcMonthPresumedAlive (Tumor) [survDatePresumedAlive/1785]
- survivalTimeDolcDayPresumedAlive (Tumor) [survDatePresumedAlive/1785]
- survivalMonths (Tumor) [survMosActiveFollowup/1784]
- survivalMonthsFlag (Tumor) [survFlagActiveFollowup/1783]
- survivalMonthsPresumedAlive (Tumor) [survMosPresumedAlive/1787]
- survivalMonthsFlagPresumedAlive (Tumor) [survFlagPresumedAlive/1786]
- sortedIndex (Tumor) [recordNumberRecode/1775]

SEER Site Recode
no option
3 versions so that would be 3 alg
input fields:
- primarySite (Tumor) [primarySite/400]
- histologyIcdO3 (Tumor) [histologicTypeIcdO3/522]
output fields:
- seerSiteRecode (Tumor, not standard) - use seerSiteRecode

ICCC
no option
normal vs extended so that would be 2 alg
input fields:
- primarySite (Tumor) (Tumor) [primarySite/400]
- histologyIcdO3 (Tumor) [histologicTypeIcdO3/522]
- behaviorIcdO3 (Tumor) [behaviorCodeIcdO3/523]
output fields:
- iccc (Tumor, not standard) - use iccc

IARC
no option
input fields:
- dateOfDiagnosisYear (Tumor) [dateOfDiagnosis/390]
- dateOfDiagnosisMonth (Tumor) [dateOfDiagnosis/390]
- dateOfDiagnosisDay (Tumor) [dateOfDiagnosis/390]
- sequenceNumber (Tumor) (Tumor) [sequenceNumberCentral/380]
- site (Tumor) [primarySite/400]
- histology (Tumor) [histologicTypeIcdO3/522]
- behavior (Tumor) [behaviorCodeIcdO3/523]
output fields:
- internationalPrimaryIndicator (Tumor, not standard) - use iarc
- siteGroup (Tumor, not standard) - use iarcSiteGroup
- histGroup (Tumor, not standard) - not iarcHistGroup

SEER Behavior Recode
no option
input fields:
- dateOfDiagnosisYear (Tumor) [dateOfDiagnosis/390]
- primarySite (Tumor) [primarySite/400]
- histologyIcdO3 (Tumor)) [histologicTypeIcdO3/522]
- behaviorIcdO3 (Tumor) [behaviorCodeIcdO3/523]
output fields:
- behaviorRecode (Tumor, not standard) - use seerBehaviorRecode

Historic Stage
Not applicable
The logic, input and output is very complicated and doesn't fit well with this new feature...

ICD
Not applicable
This module is just meant to translate one coding scheme to another for a specific field...

Site Surgery Tables
Not applicable
This module is not really an algorithm, just a data provider for a specific data item...


Properties of a NAACCR XML dictionary:
- dictionaryUri (required)
- naaccrVersion (optional)
- specificationVersion (optional)
- description (optional)

Properties of a NAACCR XML item:
- naaccrId (required)
- naaccrNum (required)
- parentXmlElement (required)
- length (required)
- dataType (optional)
- recordTypes  (optional)
- padding (optional)
- trim (optional)
- startColumn (optional)
- allowUnlimitedText (optional)
- sourceOfStandard (optional)
