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
- needsHumanReview (Patient, Non standard)
- reasonForReview (Patient, Non standard)

Death Classification
no option
2 computations so that could be 2 alg, or just 2 output fields, not sure yet
input fields:
- primarySite (Tumor)
- histologyIcdO3 (Tumor)
- sequenceNumberCentral (Tumor)
- icdRevisionNumber (Patient)
- causeOfDeath (Patient)
- dateOfLastContactYear (Patient)
output fields:
- causeSpecificDeathClassification (Patient)
- causeOtherDeathClassification (Patient)

Census Tract Poverty
option includeRecentYears (defaults to true)
input fields:
- addressAtDxState (Tumor)
- addressAtDxCounty (Tumor)
- dateOfDiagnosisYear (Tumor)
- censusTract2000 (Tumor)
- censusTract2010 (Tumor)
output fields:
- censusTractPovertyIndicator (Tumor)

Rural/Urban
no option
3 computations; I think this one has to be 3 algorithms because of the lazy initialization of the data
input fields:
- addressAtDxCounty (Tumor)
- addressAtDxState (Tumor)
- censusTract2000 (Tumor)
- censusTract2010 (Tumor)
output fields:
- urbanRuralIndicatorCode2000 (Tumor)
- urbanRuralIndicatorCode2010 (Tumor)
- urbanRuralIndicatorCode2000Percentage (Tumor, Non standard)
- urbanRuralIndicatorCode2010Percentage (Tumor, Non standard)
- ruralUrbanCommutingArea2000 (Tumor)
- ruralUrbanCommutingArea2010 (Tumor)
- ruralUrbanContinuum1993 (Tumor)
- ruralUrbanContinuum2003 (Tumor)
- ruralUrbanContinuum2013 (Tumor)

Survival Time
option endPointYear (also called study-cutoff); no default, it has to be provided
this algorithm is a patient-level algorithm but it needs all tumors
input fields:
- patientIdNumber (Patient)
- dateOfDiagnosisYear (Tumor)
- dateOfDiagnosisMonth (Tumor)
- dateOfDiagnosisDay (Tumor)
- dateOfLastContactYear (Patient)
- dateOfLastContactMonth (Patient)
- dateOfLastContactDay (Patient)
- birthYear (Patient)
- birthMonth (Patient)
- birthDay (Patient)
- vitalStatus (Patient)
- sequenceNumberCentral (Tumor)
- typeOfReportingSource (Tumor)
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
- primarySite (Tumor)
- histologyIcdO3 (Tumor)
output fields:
- seerSiteRecode (Tumor) - I can't remember if this is a standard field or not

ICCC
no option
normal vs extended so that would be 2 alg
input fields:
- primarySite (Tumor)
- histologyIcdO3 (Tumor)
- behaviorIcdO3 (Tumor)
output fields:
- iccc (Tumor) - I can't remember if this is a standard field or not

IARC
no option
input fields:
- dateOfDiagnosisYear (Tumor)
- dateOfDiagnosisMonth (Tumor)
- dateOfDiagnosisDay (Tumor)
- sequenceNumber (Tumor)
- site (Tumor)
- histology (Tumor)
- behavior (Tumor)
output fields:
- internationalPrimaryIndicator (Tumor) - not standard
- siteGroup (Tumor) - not standard
- histGroup (Tumor) - not standard

SEER Behavior Recode
no option
input fields:
- dateOfDiagnosisYear (Tumor)
- primarySite (Tumor)
- histologyIcdO3 (Tumor)
- behaviorIcdO3 (Tumor)
output fields:
- behaviorRecode (Non standard)

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
