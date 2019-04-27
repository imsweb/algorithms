Auto-registered algorithms feature

NHIA
option 0, 7 or 9 (no default, it has to be provided)
patient-level algorithm, but it does use 2 DX fields; can run on a single tumor using parent fields, or computes the best DX if patient is provided...
input fields:
- spanishHispanicOrigin (Patient)
- nameLast (Patient)
- nameMaiden (Patient)
- birthplaceCountry (Patient)
- race1 (Patient)
- sex (Patient)
- ihs (Patient)
- countyAtDx (Tumor)
- stateAtDx (Tumor)
output fields:
- nhia (Patient)

NAPIIA
no option
input fields:
- race1 (Patient)
- race2 (Patient)
- race3 (Patient)
- race4 (Patient)
- race5 (Patient)
- spanishHispanicOrigin (Patient)
- birthplaceCountry (Patient)
- sex (Patient)
- nameLast (Patient)
- nameMaiden (Patient)
- nameFirst (Patient)
output fields:
- napiiaValue (Patient)
- needsHumanReview (Non standard)
- reasonForReview (Non standard)

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
- urbanRuralIndicatorCode2000Percentage (Non standard)
- urbanRuralIndicatorCode2010Percentage (Non standard)
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
- vitalStatusRecode (Patient)
- survivalTimeDxYear (Tumor)
- survivalTimeDxMonth (Tumor)
- survivalTimeDxDay (Tumor)
- survivalTimeDolcYear (Tumor)
- survivalTimeDolcMonth (Tumor)
- survivalTimeDolcDay (Tumor)
- survivalTimeDolcYearPresumedAlive (Tumor)
- survivalTimeDolcMonthPresumedAlive (Tumor)
- survivalTimeDolcDayPresumedAlive (Tumor)
- survivalMonths (Tumor)
- survivalMonthsFlag (Tumor)
- survivalMonthsPresumedAlive (Tumor)
- survivalMonthsFlagPresumedAlive (Tumor)
- sortedIndex (Tumor) - this is actually a standard field as of NAACCR 18 (different name obviously)

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