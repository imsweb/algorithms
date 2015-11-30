#include "StageRecodes.h"

enum {PRE88AJCC , SSG2000, SSG77, HISTORIC2000, PSTSSG2000, PSTSSG77, PSTHISTORIC2000};
#define INSITU "2"
#define MALIG "3"
#define IS "0"
#define L "1"
#define RE "2"
#define RN "3"
#define RERN "4"
#define RNOS  "5"
#define D  "7"
#define NA  "8"
#define U  "9"
#define ERROR "6"

//---------------------------------------------------------------------------
StageRecodes::StageRecodes(DataDict *pDict, ZdIniSection *pSection,
                           TSessionData *pSessionData,
                           ZdProgressInterface &theProgressInterface)
   : MultiRecordRecodeFunction(pDict, pSection, pSessionData)
{
	gsRecodeName = "StageRecodes";
   Init();
   if (!InputFieldsMissing())
      Setup(pSection, theProgressInterface);
}

//---------------------------------------------------------------------------
StageRecodes::~StageRecodes()
{
   if (!InputFieldsMissing())
      {
      delete pEOD0HistoricStageTable;        pEOD0HistoricStageTable = 0;
      delete pEOD1ExtensionRecodeTable;      pEOD1ExtensionRecodeTable = 0;
      delete pEOD1HistoricStageDirectTable;  pEOD1HistoricStageDirectTable = 0;
      delete pEOD1HistoricStageFromExtensionAndNodesTable;
         pEOD1HistoricStageFromExtensionAndNodesTable = 0;
      delete pEOD1NodesRecodeTable;          pEOD1NodesRecodeTable = 0;

      delete pEOD2ExtensionRecodeTable;      pEOD2ExtensionRecodeTable = 0;
      delete pEOD2HistoricStageBladderTable; pEOD2HistoricStageBladderTable = 0;
      delete pEOD2HistoricStageFromExtensionAndNodesTable;
         pEOD2HistoricStageFromExtensionAndNodesTable = 0;
      delete pEOD2HistoricStageLungTable;    pEOD2HistoricStageLungTable = 0;
      delete pEOD2HistoricStageMelanomaTable;
         pEOD2HistoricStageMelanomaTable = 0;
      delete pEOD2NodesRecodeTable;          pEOD2NodesRecodeTable = 0;

      delete pEOD3HistoricStageTable;        pEOD3HistoricStageTable = 0;
      delete pEOD0_3HistoricStagePatchTable; pEOD0_3HistoricStagePatchTable = 0;

      delete pExtRecodeTable;                pExtRecodeTable = 0;
      delete pExt2000RecodeTable;            pExt2000RecodeTable = 0;
      delete pProstateExt2000RecodeTable;    pProstateExt2000RecodeTable = 0;
      delete pHistNodeRecodeTable;           pHistNodeRecodeTable = 0;
      delete pNode2000RecodeTable;           pNode2000RecodeTable = 0;
      delete pStageRecodeTable;              pStageRecodeTable = 0;
      delete pSummaryStage2000RecodeTable;   pSummaryStage2000RecodeTable = 0;

      delete pEOD0AJCCStageTable;            pEOD0AJCCStageTable = 0;
      delete pEOD1_3AJCCStageTable;          pEOD1_3AJCCStageTable = 0;
      delete pEOD2AJCCStageBladderTable;     pEOD2AJCCStageBladderTable = 0;
      delete pEOD2AJCCStageFromExtensionAndNodesTable;
         pEOD2AJCCStageFromExtensionAndNodesTable = 0;
      delete pEOD2AJCCStageMelanomaTable;    pEOD2AJCCStageMelanomaTable = 0;
      delete pEOD3AJCCStageBreastTable;      pEOD3AJCCStageBreastTable = 0;
      delete pAJCCStageShouldBeCodedTable;   pAJCCStageShouldBeCodedTable = 0;
      
      delete pCSExtensionTable;              pCSExtensionTable = 0;
      delete pCSNodeTable;                   pCSNodeTable = 0;
      delete pCSMetsTable;                   pCSMetsTable = 0;
      delete pCSHistStageTable;              pCSHistStageTable = 0;
      delete pLeukemiaTable;                 pLeukemiaTable = 0;
      }
}

//---------------------------------------------------------------------------
void StageRecodes::FindAndStore2000VersionRecodes(bitset<MAXNUMPATIENTRECORDS> &bsRecordsWithUnrecodedFields,
                                                  long lRecordNumber)
{
   int iSite,
       iHistology,
       iYearDx;
   unsigned uEnumValue;
       
   try
      {
      // be careful of changing the order of these assignments, esp. year, hist, & beh
      ZdString sYearDx           = gvFieldValues[1];
      iYearDx                    = atoi(sYearDx);
      ZdString sSiteNumeric      = gvFieldValues[2].GetSubstring(1);
      iSite      = atoi(sSiteNumeric);
      ZdString sHistology        = gvFieldValues[3];
      iHistology                 = atoi(sHistology); 
      ZdString sExtension        = gvFieldValues[4];
      ZdString sNodes            = gvFieldValues[5];
      ZdString sProstatePathExt  = gvFieldValues[6];
      ZdString sBehavior         = gvFieldValues[14];
      if (sBehavior == "1")
         sBehavior == "3";
      gvRecodeValues[PSTSSG2000] = " ";
      ZdVector<ZdString>          vExtensionIndices(3),
                                  vProstateExtensionIndices(3),
                                  vNodeIndices(3);


//      static ofstream out("FixLog.txt");

      try     // if it is a histology-based recode
         {
         // Node Recode Table lookup
         ZdVector< ZdPolymorph<ZdTLTLookupKey> > vNode2000RecodeTableQuery(pNode2000RecodeTable->GetQueryTemplate());
         vNode2000RecodeTableQuery.GetElement(0)->SetValue(sSiteNumeric);
         vNode2000RecodeTableQuery.GetElement(1)->SetValue(sHistology);
         vNode2000RecodeTableQuery.GetElement(2)->SetValue(sNodes);
         pNode2000RecodeTable->GetResult(vNode2000RecodeTableQuery, vNodeIndices);

         ZdVector< ZdString > vTempRecodeValue(1);
         ZdVector< ZdPolymorph<ZdTLTLookupKey> > vStageRecodeTableQuery(pStageRecodeTable->GetQueryTemplate());
        // if it is a 1995+ prostate case and the special extension is recognized,
        // change the extension to the prostate extension
        if (IsProstate(sSiteNumeric, sHistology) && 1994 < atoi(sYearDx.GetCString())) {
        	if (sProstatePathExt.GetIsEmpty()) {
               gvRecodeValues[PSTSSG2000]       = "9";
               gvRecodeValues[PSTSSG77]         = "9";
               gvRecodeValues[PSTHISTORIC2000]  = "99";
        	}
        	else {	 	 
            try
               {
               // Histology Prostate Extension Recode Table lookup
               ZdVector< ZdPolymorph<ZdTLTLookupKey> > vProstateExt2000RecodeTableQuery(pProstateExt2000RecodeTable->GetQueryTemplate());
               vProstateExt2000RecodeTableQuery.GetElement(0)->SetValue(sYearDx);
               vProstateExt2000RecodeTableQuery.GetElement(1)->SetValue(sProstatePathExt);
               pProstateExt2000RecodeTable->GetResult(vProstateExt2000RecodeTableQuery, vProstateExtensionIndices);

               // Stage Recode Table lookup
               for(unsigned u=0; u < vNodeIndices.size() && u < vProstateExtensionIndices.size(); u++)
                  {
                  uEnumValue = u + 4;
                  vStageRecodeTableQuery.GetElement(0)->SetValue(vProstateExtensionIndices[u]);
                  vStageRecodeTableQuery.GetElement(1)->SetValue(vNodeIndices[u]);
                  if (uEnumValue == PSTSSG2000 || uEnumValue == PSTSSG77)
                     pSummaryStage2000RecodeTable->GetResult(vStageRecodeTableQuery, vTempRecodeValue);
                  else
                     pStageRecodeTable->GetResult(vStageRecodeTableQuery, vTempRecodeValue);
                  gvRecodeValues[uEnumValue] = vTempRecodeValue[0];
                  }  
               }
            catch (ZdException &x)
               {
               gvRecodeValues[PSTSSG2000]       = "9";
               gvRecodeValues[PSTSSG77]         = "9";
               gvRecodeValues[PSTHISTORIC2000]  = "99";
               gvlCasesUnableToBeRecoded[PSTSSG2000]++;
               gvlCasesUnableToBeRecoded[PSTSSG77]++;
               gvlCasesUnableToBeRecoded[PSTHISTORIC2000]++;
               bsRecordsWithUnrecodedFields.set(lRecordNumber);
               }
            }
          }

         // Histology Extension Recode Table lookup
         ZdVector< ZdPolymorph<ZdTLTLookupKey> > vExt2000RecodeTableQuery(pExt2000RecodeTable->GetQueryTemplate());
         vExt2000RecodeTableQuery.GetElement(0)->SetValue(sYearDx);
         vExt2000RecodeTableQuery.GetElement(1)->SetValue(sSiteNumeric);
         vExt2000RecodeTableQuery.GetElement(2)->SetValue(sHistology);
         vExt2000RecodeTableQuery.GetElement(3)->SetValue(sExtension);
         pExt2000RecodeTable->GetResult(vExt2000RecodeTableQuery, vExtensionIndices); 

         // Breast cases sometimes depend on Behavior; this is a quick fix
         if (IsBreast(sSiteNumeric) && sExtension == "05")
            {
            if (sBehavior == "2")
               {
               vExtensionIndices[0] = "1";
               vExtensionIndices[1] = "1";
               vExtensionIndices[2] = "1";
               }
            else if (sBehavior == "3")
               {
               vExtensionIndices[0] = "2";
               vExtensionIndices[1] = "2";
               vExtensionIndices[2] = "2";
               }
            }

         // This is the special case for melanoma
         if (((440 <= iSite && iSite <= 449) ||
             (510 <= iSite && iSite <= 519) ||
             (600 <= iSite && iSite <= 602) ||
             (608 <= iSite && iSite <= 609) ||
             (iSite == 632)) &&
             8720 <= iHistology && iHistology <= 8790 &&
             sNodes == "3" && sExtension == "99" )
            {
            vExtensionIndices[2] = "3";
            }

         // Stage Recode Table lookup
         for(unsigned u=0; u < vNodeIndices.size() && u < vExtensionIndices.size(); u++)
            {
            uEnumValue = u + 1;
            vStageRecodeTableQuery.GetElement(0)->SetValue(vExtensionIndices[u]);
            vStageRecodeTableQuery.GetElement(1)->SetValue(vNodeIndices[u]);
            if (uEnumValue == SSG2000 || uEnumValue == SSG77)
               pSummaryStage2000RecodeTable->GetResult(vStageRecodeTableQuery, vTempRecodeValue);
            else
               pStageRecodeTable->GetResult(vStageRecodeTableQuery, vTempRecodeValue);

            // Special Bladder Processing  (these tests are written twice in this program)
            if (IsBladder(sSiteNumeric) && (uEnumValue == HISTORIC2000)
                && (vTempRecodeValue[0] == "00" || vTempRecodeValue[0] == "10"))
               vTempRecodeValue[0] = "11";
            if (IsBladder(sSiteNumeric) && uEnumValue == SSG77 && vTempRecodeValue[0] == "0")
               vTempRecodeValue[0] = "1";

            // Special Brain Processing
            if (uEnumValue == HISTORIC2000 && IsBrain(sSiteNumeric) && (iHistology < 9530 || 9539 < iHistology && iHistology < 9590))
               vTempRecodeValue[0] = "90";
            gvRecodeValues[uEnumValue] = vTempRecodeValue[0]; 
            }
         }
      catch (ZdException &x)
         {
//         out << sYearDx << "\t" << sSiteNumeric << "\t" << sHistology << "\t" << sExtension << "\t" << sNodes << "\t" << vExtensionIndices[2] << "\t" << vNodeIndices[2] << "\n";
         gvRecodeValues[SSG2000]       = "9";
         gvRecodeValues[SSG77]         = "9";
         gvRecodeValues[HISTORIC2000]  = "99";
         gvlCasesUnableToBeRecoded[SSG2000]++;
         gvlCasesUnableToBeRecoded[SSG77]++;
         gvlCasesUnableToBeRecoded[HISTORIC2000]++;
         bsRecordsWithUnrecodedFields.set(lRecordNumber);
         }

      // Choose between the clinical and the pathological Summary Stage 2000
      if (gvRecodeValues[PSTSSG2000] != " ")
         {
         if (gvRecodeValues[SSG2000] == gvRecodeValues[PSTSSG2000])
            gvRecodeValues[SSG2000] = gvRecodeValues[PSTSSG2000]; // useless line
         else if (gvRecodeValues[SSG2000] == "9")
            gvRecodeValues[SSG2000] = gvRecodeValues[PSTSSG2000];
         else if (gvRecodeValues[PSTSSG2000] == "9")
            gvRecodeValues[SSG2000] = gvRecodeValues[SSG2000]; // useless line
         else if (gvRecodeValues[PSTSSG2000] > gvRecodeValues[SSG2000])
            gvRecodeValues[SSG2000] = gvRecodeValues[PSTSSG2000];
         else     // clinical is bigger than the prostate pathological
            gvRecodeValues[SSG2000] = gvRecodeValues[SSG2000]; // useless line
         }
      // end of choice between the clinical and the pathological Summary Stage 2000


      // Quick fix: Certain cases are not consistent with old codes, leave them blank
      if ((8000 <= iHistology && iHistology <= 9139) || (9141 <= iHistology && iHistology <= 9589))
         {
         if ((((79 <= iSite && iSite <= 81) || (88 <= iSite && iSite <=89 )) && (1984 <= iYearDx && iYearDx <= 1997)) ||
             ((90 <= iSite && iSite <= 109) && (1984 <= iYearDx && iYearDx <= 1988)) ||
             ((iSite == 619) && (1984 <= iYearDx && iYearDx <= 1997))) 
            	gvRecodeValues[SSG2000] = " ";
         }  
         
 	 		if (((8000 <= iHistology && iHistology <= 9139) || (9141 <= iHistology && iHistology <= 9589)) && iSite == 619 && iYearDx < 1995) 	 			
 	 			gvRecodeValues[SSG77] = " ";
         
      // end of Quick fix
      
           

      }
   catch (ZdException &x)
      {   
      x.AddCallpath("FindAndStore2000VersionRecodes()", "StageRecodes");
      throw;
      }
}

//---------------------------------------------------------------------------
void StageRecodes::Init()
{
   pEOD0HistoricStageTable                      = 0;
   pEOD1ExtensionRecodeTable                    = 0;
   pEOD1HistoricStageDirectTable                = 0;
   pEOD1HistoricStageFromExtensionAndNodesTable = 0;
   pEOD1NodesRecodeTable                        = 0;
   pEOD2ExtensionRecodeTable                    = 0;
   pEOD2HistoricStageBladderTable               = 0;
   pEOD2HistoricStageFromExtensionAndNodesTable = 0;
   pEOD2HistoricStageLungTable                  = 0;
   pEOD2HistoricStageMelanomaTable              = 0;
   pEOD2NodesRecodeTable                        = 0;
   pEOD3HistoricStageTable                      = 0;
   pEOD0_3HistoricStagePatchTable               = 0;
   pExtRecodeTable 	                           = 0;
   pExt2000RecodeTable 	                        = 0;
   pProstateExt2000RecodeTable                  = 0;
   pHistNodeRecodeTable                         = 0;
   pNode2000RecodeTable                         = 0;
   pStageRecodeTable 	                        = 0;
   pSummaryStage2000RecodeTable                 = 0;
   pEOD0AJCCStageTable                          = 0;
   pEOD1_3AJCCStageTable                        = 0;
   pEOD2AJCCStageBladderTable                   = 0;
   pEOD2AJCCStageFromExtensionAndNodesTable     = 0;
   pEOD2AJCCStageMelanomaTable                  = 0;
   pEOD3AJCCStageBreastTable                    = 0;
   pAJCCStageShouldBeCodedTable                 = 0;
   pCSExtensionTable = 0;
   pCSNodeTable = 0;
   pCSMetsTable = 0;
   pCSHistStageTable = 0;
   pLeukemiaTable = 0;
}
//---------------------------------------------------------------------------

bool StageRecodes::IsBrain(ZdString sSiteNumeric)
   {
   bool bIsBrain = false;
   short wSite = atoi(sSiteNumeric.GetCString());
   if (710 <= wSite && wSite <= 719)
      bIsBrain = true;
   return bIsBrain;
   }

//---------------------------------------------------------------------------
bool StageRecodes::IsBreast(ZdString sSite)
{
   bool bIsBreast = false;
   short wSite = atoi(sSite.GetCString());
   if (500 <= wSite && wSite <= 509)
      bIsBreast = true;
   return bIsBreast;
}

//---------------------------------------------------------------------------

bool StageRecodes::IsLung(ZdString sSiteNumeric)
   {
   bool bIsLung = false;
   short wSite = atoi(sSiteNumeric.GetCString());
   if (340 <= wSite && wSite <= 349)
      bIsLung = true;
   return bIsLung;
   }

//---------------------------------------------------------------------------

void StageRecodes::RecodeRecord(bitset<MAXNUMPATIENTRECORDS> &bsRecordsWithUnrecodedFields,
                                long lRecordNumber)
{
		
   bool  bFoundAJCCStage = false;
   short wYearDx;
   short wSurgType;
   short wSurgPrimSite; 
   try
      {
      ZdVector< ZdString >	vDummyVector(1);
      ZdString sExtensionRecode;
      ZdString sNodesRecode;

      // get the fields and put them into variables
      ZdString sReportingSource  = gvFieldValues[0];
      ZdString sYearDx           = gvFieldValues[1];
      ZdString sSiteNumeric      = gvFieldValues[2].GetSubstring(1);
      int iSite = atoi(sSiteNumeric.GetCString());
      ZdString sHistology        = gvFieldValues[3];
      int iHistology = atoi(sHistology.GetCString());
      ZdString sEODCodingSystem  = gvFieldValues[7];
      ZdString sOld4DigitSize    = gvFieldValues[10];
      ZdString sOld13Digit       = gvFieldValues[11];
      ZdString sOld2Digit        = gvFieldValues[12];
      ZdString sBeh              = gvFieldValues[14];
      ZdString sCSExtension      = gvFieldValues[15];
      if (giVersion < 12) {
      	if (sCSExtension == "99") sCSExtension = "999";
      	else if (sCSExtension == "88") sCSExtension = "988";
      	else sCSExtension << sCSExtension << "0";
      }
      int iCSExt = atoi(sCSExtension.GetCString());
      ZdString sCSNodes          = gvFieldValues[16];
      if (giVersion < 12) {
      	if (sCSNodes == "99") sCSNodes = "999";
      	else if (sCSNodes == "88") sCSNodes = "988";
      	else sCSNodes << sCSNodes << "0";
      }      
      int iCSNodes = atoi(sCSNodes.GetCString());
      ZdString sCSMets           = gvFieldValues[17];
      if (giVersion < 12 & sCSMets == "88") sCSMets = "98";
      int iCSMets = atoi(sCSMets.GetCString());
      ZdString sCSSSF1           = gvFieldValues[18];
      int iCSSSF1 = atoi(sCSSSF1.GetCString());
      ZdString sCSSSF2           = gvFieldValues[19];
      int iCSSSF2 = atoi(sCSSSF2.GetCString());
      ZdString sCSSchema         = gvFieldValues[20];
      int iCSSchema = atoi(sCSSchema.GetCString());
      
      wYearDx  = atoi(sYearDx.GetCString());
      gbIsLeuk = false;
            
      try {
      	ZdVector<ZdString> vLeukValue(1);
      	ZdVector< ZdPolymorph<ZdTLTLookupKey> > vLeukemiaTableQuery(pLeukemiaTable->GetQueryTemplate());
      	vLeukemiaTableQuery.GetElement(0)->SetValue(sSiteNumeric);
      	vLeukemiaTableQuery.GetElement(1)->SetValue(sHistology);
      	pLeukemiaTable->GetResult(vLeukemiaTableQuery, vLeukValue);
      	if (vLeukValue.GetElement(0) == "1") gbIsLeuk = true;
      	else gbIsLeuk = false;
      }
			catch (ZdException &x) {
				gbIsLeuk=false;
      }
      

      // if blank coding system is found, all the recode values should also be blank
      if (sEODCodingSystem == "" && wYearDx < 2004)
         {
         bsRecordsWithUnrecodedFields.set(lRecordNumber);
         }
      else if (AreFlagsSet())
         {
         if (wYearDx < 2004) {
         	gvRecodeValues[SSG2000] = "9";
         	gvRecodeValues[SSG77] = "9";
         }
         gvRecodeValues[HISTORIC2000] = "99";
         if (wYearDx < 1988)
            gvRecodeValues[PRE88AJCC] = "99";
         IncrementErrorCount();
         bsRecordsWithUnrecodedFields.set(lRecordNumber);
         }
      else if (wYearDx >= 2004) {
      	if (!gbIsLeuk && sReportingSource == "7") gvRecodeValues[HISTORIC2000] = "99";
      	else {
      		try {
						ZdVector<ZdString>	vCSExtValue(1),
		    	                      vCSNodeValue(1),
		    	                      vCSMetValue(1),
		    	                      vCSStageValue(1);
		    	                      
						ZdString sCSExtRecode, sCSNodeRecode, sCSMetsRecode;
		    	                      
        	
      			ZdVector< ZdPolymorph<ZdTLTLookupKey> > vCSExtTableQuery(pCSExtensionTable->GetQueryTemplate());
      			ZdVector< ZdPolymorph<ZdTLTLookupKey> > vCSNodeTableQuery(pCSNodeTable->GetQueryTemplate());
      			ZdVector< ZdPolymorph<ZdTLTLookupKey> > vCSMetTableQuery(pCSMetsTable->GetQueryTemplate());
      			ZdVector< ZdPolymorph<ZdTLTLookupKey> > vCSStageTableQuery(pCSHistStageTable->GetQueryTemplate()); 
      			      			
      			vCSExtTableQuery.GetElement(0)->SetValue(sCSSchema);
      			vCSExtTableQuery.GetElement(1)->SetValue(sCSExtension); 
      			pCSExtensionTable->GetResult(vCSExtTableQuery, vCSExtValue);
      			sCSExtRecode = vCSExtValue.GetElement(0); 
        		
        		//Special case for longevity consistency
        		if ((iHistology == 9823 || iHistology == 9827) && iCSExt == 800
        			   && ((0 <= iSite && iSite <= 419) || (422 <= iSite && iSite <= 423) || (425 <= iSite && iSite <= 809)))         		   
        			sCSExtRecode = U;
        			
        		//Special case for corpus schemas per Lynn Ries/Jennifer Ruhl starting Nov11 sub
        		if (iCSSchema == 62 && sCSExtRecode == L && iCSSSF2 == 10)
        			sCSExtRecode = RE; 
        			
      			
        		
						//Special Breast Processing
						if (iCSSchema == 58 && (iCSExt == 50 || iCSExt == 70)) {
							if (sBeh == INSITU) sCSExtRecode = IS;
							else if (sBeh == MALIG) sCSExtRecode = L;
							else throw new ZdException("Bad Breast Behavior");
						}					
						//Special Pleura Processing
						
						if (iCSSchema == 49) {
							if (100 <= iCSExt && iCSExt <= 305) {
								switch (iCSSSF1) {
									case 0 : 
										sCSExtRecode = L;
										break;
									case 10 : 
										sCSExtRecode = L;
									  break;
									case 20 : 
										sCSExtRecode = D;
										break;
									case 30 :
										sCSExtRecode = D;
										break;
									case 999 :
										sCSExtRecode = L;
										break;
									default:
										ZdString err;
										err << "Bad Pleura Extension/SSF1(" << sCSExtension << "/" << sCSSSF1 << ")\n";
										ZdException::Generate(err.GetCString(),"StageRecodes");
								}							
							}
							else if (420 <= iCSExt && iCSExt <= 650) {
								switch (iCSSSF1) {
									case 0 : 
										sCSExtRecode = RE;
										break;
									case 10 : 
										sCSExtRecode = RE;
									  break;
									case 20 : 
										sCSExtRecode = D;
										break;
									case 30 :
										sCSExtRecode = D;
										break;
									case 999 :
										sCSExtRecode = RE;
										break;
									default:
										ZdString err;
										err << "Bad Pleura Extension/SSF1(" << sCSExtension << "/" << sCSSSF1 << ")\n";
										ZdException::Generate(err.GetCString(),"StageRecodes");
								}													
							}
							else if (690 <= iCSExt && iCSExt <= 850) {
								switch (iCSSSF1) {
									case 0 : 
										sCSExtRecode = D;
										break;
									case 10 : 
										sCSExtRecode = D;
									  break;
									case 20 : 
										sCSExtRecode = D;
										break;
									case 30 :
										sCSExtRecode = D;
										break;
									case 999 :
										sCSExtRecode = D;
										break;
									default:
										ZdString err;
										err << "Bad Pleura Extension/SSF1(" << sCSExtension << "/" << sCSSSF1 << ")\n";
										ZdException::Generate(err.GetCString(),"StageRecodes");
								}													
							}
							else if (950 <= iCSExt && iCSExt <= 999) {
								switch (iCSSSF1) {
									case 0 : 
										sCSExtRecode = U;
										break;
									case 10 : 
										sCSExtRecode = U;
									  break;
									case 20 : 
										sCSExtRecode = D;
										break;
									case 30 :
										sCSExtRecode = D;
										break;
									case 999 :
										sCSExtRecode = U;
										break;
									default:
										ZdString err;
										err << "Bad Pleura Extension/SSF1(" << sCSExtension << "/" << sCSSSF1 << ")\n";
										ZdException::Generate(err.GetCString(),"StageRecodes");
								}							
							}
							else {
								ZdString err;
								err << "Bad Pleura Extension/SSF1(" << sCSExtension << "/" << sCSSSF1 << ")\n";
								ZdException::Generate(err.GetCString(),"StageRecodes");
            	}
						}
						        		
      			vCSNodeTableQuery.GetElement(0)->SetValue(sCSSchema);
      			vCSNodeTableQuery.GetElement(1)->SetValue(sCSNodes);
      			pCSNodeTable->GetResult(vCSNodeTableQuery, vCSNodeValue);
      			sCSNodeRecode = vCSNodeValue.GetElement(0); 
        		
        		vCSMetTableQuery.GetElement(0)->SetValue(sCSSchema);
        		vCSMetTableQuery.GetElement(1)->SetValue(sCSMets);
        		pCSMetsTable->GetResult(vCSMetTableQuery, vCSMetValue);
        		sCSMetsRecode = vCSMetValue.GetElement(0);
        		        		
        		
        		vCSStageTableQuery.GetElement(0)->SetValue(sCSExtRecode);
        		vCSStageTableQuery.GetElement(1)->SetValue(sCSNodeRecode);
        		vCSStageTableQuery.GetElement(2)->SetValue(sCSMetsRecode);
        		pCSHistStageTable->GetResult(vCSStageTableQuery, vCSStageValue);
        		gvRecodeValues[HISTORIC2000] = vCSStageValue.GetElement(0);
        	      
      		}
      		catch (ZdException &x) {
      			gvRecodeValues[HISTORIC2000] = "60";
      		  bsRecordsWithUnrecodedFields.set(lRecordNumber);
      		}
      	}
    	}
      else
         {
         if(sEODCodingSystem == "4")
            RecodeEODScheme4Stages(bsRecordsWithUnrecodedFields, lRecordNumber);

         // the following else section applies only to EOD schemes 0-3
         // it consists of special Historic Stage recodes from Lynn Ries
         // maybe add to the tables??
         else // EOD Coding System is 0-3
            {
            short wHistology = atoi(sHistology.GetCString());
            short wSite = atoi(sSiteNumeric.GetCString());

            ZdVector< ZdString >    vDummyVector(1);
            ZdVector< ZdPolymorph<ZdTLTLookupKey> > vEOD0_3HistoricStagePatchTableQuery(pEOD0_3HistoricStagePatchTable->GetQueryTemplate());
            vEOD0_3HistoricStagePatchTableQuery.GetElement(0)->SetValue(sReportingSource);
            vEOD0_3HistoricStagePatchTableQuery.GetElement(1)->SetValue(sSiteNumeric);
            vEOD0_3HistoricStagePatchTableQuery.GetElement(2)->SetValue(sHistology);
            try
               {
               pEOD0_3HistoricStagePatchTable->GetResult(vEOD0_3HistoricStagePatchTableQuery, vDummyVector);
               gvRecodeValues[HISTORIC2000] = vDummyVector[0];
               }
            catch (ZdException &x)
               {
               if (sEODCodingSystem == "3")
                  RecodeEODScheme3HistoricStage(&sExtensionRecode, &sNodesRecode,
                                                bsRecordsWithUnrecodedFields,
                                                lRecordNumber);
               else if (sEODCodingSystem == "2")
                  RecodeEODScheme2HistoricStage(&sExtensionRecode, &sNodesRecode,
                                                bsRecordsWithUnrecodedFields,
                                                lRecordNumber);
               else if(sEODCodingSystem == "1")
                  RecodeEODScheme1HistoricStage(&sExtensionRecode, &sNodesRecode,
                                                bsRecordsWithUnrecodedFields,
                                                lRecordNumber);
               else if (sEODCodingSystem == "0")
                  RecodeEODScheme0HistoricStage(bsRecordsWithUnrecodedFields,
                                                lRecordNumber);
               }

            // Here is the start of AJCC Recode
            try
               {
        	      ZdVector< ZdPolymorph<ZdTLTLookupKey> > vAJCCStageShouldBeCodedTableQuery( pAJCCStageShouldBeCodedTable->GetQueryTemplate() );
               vAJCCStageShouldBeCodedTableQuery.GetElement(0)->SetValue(sEODCodingSystem);
               vAJCCStageShouldBeCodedTableQuery.GetElement(1)->SetValue(sHistology);
               vAJCCStageShouldBeCodedTableQuery.GetElement(2)->SetValue(sSiteNumeric);
               pAJCCStageShouldBeCodedTable->GetResult(vAJCCStageShouldBeCodedTableQuery, vDummyVector);
               // the value obtained by the previous lookup is arbitrary
               // if the table found a value, proceed to find a value for AJCC Stage Recode
               // otherwise, the exception will be thrown and no value is placed in AJCC Stage Recode

               if ( (sEODCodingSystem == "0") && (0 <= wSite && wSite <= 149)
                    && (8000 <= wHistology && wHistology <= 9589) )
                  {
                  ZdString sOld2Digit1       = sOld2Digit.GetSubstring(0,1);
                  ZdString sOld2Digit2       = sOld2Digit.GetSubstring(1,1);

                  try
                     {
            	      ZdVector< ZdPolymorph<ZdTLTLookupKey> > vEOD0AJCCStageTableQuery( pEOD0AJCCStageTable->GetQueryTemplate() );
                     vEOD0AJCCStageTableQuery.GetElement(0)->SetValue(sOld2Digit1);
                     vEOD0AJCCStageTableQuery.GetElement(1)->SetValue(sOld2Digit2);
                     gvRecodeValues[PRE88AJCC] = pEOD0AJCCStageTable->GetResult(vEOD0AJCCStageTableQuery,
                        vDummyVector).GetElement(0);
                     bFoundAJCCStage = true;
                     }
                  catch(ZdException &x)
                     {
                       gvRecodeValues[PRE88AJCC] = "";
                     }
                  }
               else if ( (8000 <= wHistology && wHistology <= 9589)
                         && ( 1 <= atoi(sEODCodingSystem.GetCString()) && atoi(sEODCodingSystem.GetCString()) <= 3) )
                  {
                  try
                     {
            	      ZdVector< ZdPolymorph<ZdTLTLookupKey> > vEOD1_3AJCCStageTableQuery( pEOD1_3AJCCStageTable->GetQueryTemplate() );
                     vEOD1_3AJCCStageTableQuery.GetElement(0)->SetValue(sSiteNumeric);
                     vEOD1_3AJCCStageTableQuery.GetElement(1)->SetValue(sExtensionRecode);
                     vEOD1_3AJCCStageTableQuery.GetElement(2)->SetValue(sNodesRecode);
                     gvRecodeValues[PRE88AJCC] = pEOD1_3AJCCStageTable->GetResult(vEOD1_3AJCCStageTableQuery,
                        vDummyVector).GetElement(0);
                     bFoundAJCCStage = true;
                     }
                  catch(ZdException &x)
                     {
                       gvRecodeValues[PRE88AJCC] = "";
                     }
                  }

               if (bFoundAJCCStage == false)
                  {
                  if ( (sEODCodingSystem == "3") && 500 <= wSite && wSite <= 509 )
                     {
                     try
                        {
               	      ZdVector< ZdPolymorph<ZdTLTLookupKey> > vEOD3AJCCStageBreastTableQuery( pEOD3AJCCStageBreastTable->GetQueryTemplate() );
                        vEOD3AJCCStageBreastTableQuery.GetElement(0)->SetValue(sHistology);
                        vEOD3AJCCStageBreastTableQuery.GetElement(1)->SetValue(sOld4DigitSize);
                        vEOD3AJCCStageBreastTableQuery.GetElement(2)->SetValue(sExtensionRecode);
                        vEOD3AJCCStageBreastTableQuery.GetElement(3)->SetValue(sNodesRecode);
                        gvRecodeValues[PRE88AJCC] = pEOD3AJCCStageBreastTable->GetResult(vEOD3AJCCStageBreastTableQuery,
                           vDummyVector).GetElement(0);
                        bFoundAJCCStage = true;
                        }
                     catch(ZdException &x)
                        {
                          gvRecodeValues[PRE88AJCC] = "";
                        }
                     }
                  else if (sEODCodingSystem == "2")
                     {
                     ZdString sOld13Digit01     = sOld13Digit.GetSubstring(0,1);
                     ZdString sOld13Digit02     = sOld13Digit.GetSubstring(1,1);
                     ZdString sOld13Digit05     = sOld13Digit.GetSubstring(4,1);
                     ZdString sOld13Digit06     = sOld13Digit.GetSubstring(5,1);
                     ZdString sOld13Digit07     = sOld13Digit.GetSubstring(6,1);
                     ZdString sOld13Digit09     = sOld13Digit.GetSubstring(8,1);
                     ZdString sOld13Digit10     = sOld13Digit.GetSubstring(9,1);
                     ZdString sOld13Digit11     = sOld13Digit.GetSubstring(10,1);
                     ZdString sOld13Digit12     = sOld13Digit.GetSubstring(11,1);
                     ZdString sOld13Digit13     = sOld13Digit.GetSubstring(12,1);

                     if ( (8720 <= wHistology <= 8799)
                               && ( (440<=wSite && wSite<=447) || (510<=wSite && wSite<=519)
                                    || wSite==600 || wSite==601 || wSite==608 || wSite==609 ) )
                        {
                        try
                           {
                  	      ZdVector< ZdPolymorph<ZdTLTLookupKey> > vEOD2AJCCStageMelanomaTableQuery( pEOD2AJCCStageMelanomaTable->GetQueryTemplate() );
                           vEOD2AJCCStageMelanomaTableQuery.GetElement(0)->SetValue(sOld13Digit02);
                           vEOD2AJCCStageMelanomaTableQuery.GetElement(1)->SetValue(sOld13Digit05);
                           vEOD2AJCCStageMelanomaTableQuery.GetElement(2)->SetValue(sOld13Digit06);
                           vEOD2AJCCStageMelanomaTableQuery.GetElement(3)->SetValue(sOld13Digit09);
                           vEOD2AJCCStageMelanomaTableQuery.GetElement(4)->SetValue(sOld13Digit10);
                           vEOD2AJCCStageMelanomaTableQuery.GetElement(5)->SetValue(sOld13Digit11);
                           vEOD2AJCCStageMelanomaTableQuery.GetElement(6)->SetValue(sOld13Digit12);
                           vEOD2AJCCStageMelanomaTableQuery.GetElement(7)->SetValue(sOld13Digit13);
                           gvRecodeValues[PRE88AJCC] = pEOD2AJCCStageMelanomaTable->GetResult(vEOD2AJCCStageMelanomaTableQuery,
                              vDummyVector).GetElement(0);
                           bFoundAJCCStage = true;
                           }
                        catch(ZdException &x)
                           {}
                        }
                     else if ( (8000 <= wHistology && wHistology <= 9589)
                               && IsBladder(sSiteNumeric) )
                        {
                        try
                           {
                  	      ZdVector< ZdPolymorph<ZdTLTLookupKey> > vEOD2AJCCStageBladderTableQuery( pEOD2AJCCStageBladderTable->GetQueryTemplate() );
                           vEOD2AJCCStageBladderTableQuery.GetElement(0)->SetValue(sOld13Digit05);
                           vEOD2AJCCStageBladderTableQuery.GetElement(1)->SetValue(sOld13Digit06);
                           vEOD2AJCCStageBladderTableQuery.GetElement(2)->SetValue(sOld13Digit07);
                           vEOD2AJCCStageBladderTableQuery.GetElement(3)->SetValue(sOld13Digit09);
                           vEOD2AJCCStageBladderTableQuery.GetElement(4)->SetValue(sOld13Digit10);
                           vEOD2AJCCStageBladderTableQuery.GetElement(5)->SetValue(sOld13Digit11);
                           vEOD2AJCCStageBladderTableQuery.GetElement(6)->SetValue(sOld13Digit12);
                           vEOD2AJCCStageBladderTableQuery.GetElement(7)->SetValue(sOld13Digit13);
                           gvRecodeValues[PRE88AJCC] = pEOD2AJCCStageBladderTable->GetResult(vEOD2AJCCStageBladderTableQuery,
                              vDummyVector).GetElement(0);
                           bFoundAJCCStage = true;
                           }
                        catch(ZdException &x)
                           {}
                        }
                     else
                        {
                        try
                           {
                           ZdVector< ZdPolymorph<ZdTLTLookupKey> > vEOD2AJCCStageFromExtensionAndNodesTableQuery(
                              pEOD2AJCCStageFromExtensionAndNodesTable->GetQueryTemplate() );
                           vEOD2AJCCStageFromExtensionAndNodesTableQuery.GetElement(0)->SetValue(sSiteNumeric);
                           vEOD2AJCCStageFromExtensionAndNodesTableQuery.GetElement(1)->SetValue(sHistology);
                           vEOD2AJCCStageFromExtensionAndNodesTableQuery.GetElement(2)->SetValue(sOld13Digit01);
                           vEOD2AJCCStageFromExtensionAndNodesTableQuery.GetElement(3)->SetValue(sOld13Digit02);
                           vEOD2AJCCStageFromExtensionAndNodesTableQuery.GetElement(4)->SetValue(sExtensionRecode);
                           vEOD2AJCCStageFromExtensionAndNodesTableQuery.GetElement(5)->SetValue(sNodesRecode);
                           gvRecodeValues[PRE88AJCC] = pEOD2AJCCStageFromExtensionAndNodesTable->GetResult(vEOD2AJCCStageFromExtensionAndNodesTableQuery,
                              vDummyVector).GetElement(0);
                           bFoundAJCCStage = true;
                           }
                        catch(ZdException &x)
                           {}
                        }
                     }
                  }

               if (bFoundAJCCStage == false && (wYearDx < 1988) )
                  {
                  gvRecodeValues[PRE88AJCC] = "99";
                  gvlCasesUnableToBeRecoded[PRE88AJCC]++;
                  bsRecordsWithUnrecodedFields.set(lRecordNumber);
                  }
               }
            catch (ZdException &x)
               {
               // AJCC Stage Recode should be left blank,
               // the inputs were was not in the first table
               }
            }

         // This is a fix that Jennifer had me add on April 10, 2003
         if (wYearDx < 1983 && sHistology == "9140")
            gvRecodeValues[HISTORIC2000] = "90";
         // This the end of the fix that Jennifer had me add on April 10, 2003

         } 
         
      }
   catch (ZdException &x)
      {
      x.AddCallpath("RecodeRecord()", "StageRecodes");
      throw;
      }
}

//---------------------------------------------------------------------------
void StageRecodes::RecodeEODScheme0HistoricStage(bitset<MAXNUMPATIENTRECORDS> &bsRecordsWithUnrecodedFields,
                                                 long lRecordNumber)
{
   try
      {
      ZdString sSiteNumeric = gvFieldValues[2].GetSubstring(1);
      ZdString sHistology   = gvFieldValues[3];
      ZdString sOld2Digit   = gvFieldValues[12];
      ZdString sOld2Digit1  = sOld2Digit.GetSubstring(0,1);
      ZdString sOld2Digit2  = sOld2Digit.GetSubstring(1,1);
      ZdVector< ZdString >	vDummyVector(1);

      try
         {
  	      ZdVector< ZdPolymorph<ZdTLTLookupKey> > vEOD0HistoricStageTableQuery( pEOD0HistoricStageTable->GetQueryTemplate() );
         vEOD0HistoricStageTableQuery.GetElement(0)->SetValue(sSiteNumeric);
         vEOD0HistoricStageTableQuery.GetElement(1)->SetValue(sHistology);
         vEOD0HistoricStageTableQuery.GetElement(2)->SetValue(sOld2Digit1);
         vEOD0HistoricStageTableQuery.GetElement(3)->SetValue(sOld2Digit2);
         pEOD0HistoricStageTable->GetResult(vEOD0HistoricStageTableQuery, vDummyVector);
         gvRecodeValues[HISTORIC2000] = vDummyVector[0];
         }
      catch(ZdException &x)
         {
         gvRecodeValues[HISTORIC2000] = "99";
         gvlCasesUnableToBeRecoded[HISTORIC2000]++;
         bsRecordsWithUnrecodedFields.set(lRecordNumber);
         }
      }
   catch (ZdException &x)
      {
      x.AddCallpath("RecodeEODScheme0HistoricStage()", "StageRecodes");
      throw;
      }
}

//---------------------------------------------------------------------------
void StageRecodes::RecodeEODScheme1HistoricStage(ZdString *sExtensionRecode,
                                                 ZdString *sNodesRecode,
                                                 bitset<MAXNUMPATIENTRECORDS> &bsRecordsWithUnrecodedFields,
                                                 long lRecordNumber)
{
   try
      {
      ZdString sSiteNumeric = gvFieldValues[2].GetSubstring(1);
      ZdString sHistology   = gvFieldValues[3];
      ZdString sOld2Digit   = gvFieldValues[12];
      ZdString sOld2Digit1  = sOld2Digit.GetSubstring(0,1);
      ZdString sOld2Digit2  = sOld2Digit.GetSubstring(1,1);
      short wHistology = atoi(sHistology.GetCString());
      ZdVector< ZdString >	vDummyVector(1);

      try
         {
      	ZdVector< ZdPolymorph<ZdTLTLookupKey> > vEOD1ExtensionRecodeTableQuery( pEOD1ExtensionRecodeTable->GetQueryTemplate() );
         vEOD1ExtensionRecodeTableQuery.GetElement(0)->SetValue(sHistology);
         vEOD1ExtensionRecodeTableQuery.GetElement(1)->SetValue(sSiteNumeric);
         vEOD1ExtensionRecodeTableQuery.GetElement(2)->SetValue(sOld2Digit1);
         vEOD1ExtensionRecodeTableQuery.GetElement(3)->SetValue(sOld2Digit2);
         (*sExtensionRecode) = pEOD1ExtensionRecodeTable->GetResult(vEOD1ExtensionRecodeTableQuery, vDummyVector).GetElement(0);
         }
      catch (ZdException &x)
         {
         (*sExtensionRecode) = "99";
         }
      if( 8000 <= wHistology && wHistology <= 9589 )
         {
         try
            {
   	      ZdVector< ZdPolymorph<ZdTLTLookupKey> > vEOD1NodesRecodeTableQuery( pEOD1NodesRecodeTable->GetQueryTemplate() );
            vEOD1NodesRecodeTableQuery.GetElement(0)->SetValue(sSiteNumeric);
            vEOD1NodesRecodeTableQuery.GetElement(1)->SetValue(sOld2Digit1);
            vEOD1NodesRecodeTableQuery.GetElement(2)->SetValue(sOld2Digit2);
            (*sNodesRecode) = pEOD1NodesRecodeTable->GetResult(vEOD1NodesRecodeTableQuery, vDummyVector).GetElement(0);
            }
         catch (ZdException &x)
            {
            (*sNodesRecode) = "99";
            }
         }
      try
         {
	      ZdVector< ZdPolymorph<ZdTLTLookupKey> > vEOD1HistoricStageDirectTableQuery( pEOD1HistoricStageDirectTable->GetQueryTemplate() );
         vEOD1HistoricStageDirectTableQuery.GetElement(0)->SetValue(sSiteNumeric);
         vEOD1HistoricStageDirectTableQuery.GetElement(1)->SetValue(sHistology);
         vEOD1HistoricStageDirectTableQuery.GetElement(2)->SetValue(sOld2Digit1);
         vEOD1HistoricStageDirectTableQuery.GetElement(3)->SetValue(sOld2Digit2);
         pEOD1HistoricStageDirectTable->GetResult(vEOD1HistoricStageDirectTableQuery, vDummyVector);
         gvRecodeValues[HISTORIC2000] = vDummyVector[0];
         }
      catch(ZdException &x)
         {
         try
            {
   	      ZdVector< ZdPolymorph<ZdTLTLookupKey> > vEOD1HistoricStageFromExtensionAndNodesTableQuery(
               pEOD1HistoricStageFromExtensionAndNodesTable->GetQueryTemplate() );
            vEOD1HistoricStageFromExtensionAndNodesTableQuery.GetElement(0)->SetValue(sSiteNumeric);
            vEOD1HistoricStageFromExtensionAndNodesTableQuery.GetElement(1)->SetValue(sHistology);
            vEOD1HistoricStageFromExtensionAndNodesTableQuery.GetElement(2)->SetValue(sExtensionRecode->GetCString());
            vEOD1HistoricStageFromExtensionAndNodesTableQuery.GetElement(3)->SetValue(sNodesRecode->GetCString());
            pEOD1HistoricStageFromExtensionAndNodesTable->GetResult(vEOD1HistoricStageFromExtensionAndNodesTableQuery, vDummyVector);
            gvRecodeValues[HISTORIC2000] = vDummyVector[0];
            }
         catch (ZdException &x)
            {
            gvRecodeValues[HISTORIC2000] = "99";
            gvlCasesUnableToBeRecoded[HISTORIC2000]++;
            bsRecordsWithUnrecodedFields.set(lRecordNumber);
            }
         }
      }
   catch (ZdException &x)
      {
      x.AddCallpath("RecodeEODScheme1HistoricStage()", "StageRecodes");
      throw;
      }
}

//---------------------------------------------------------------------------
void StageRecodes::RecodeEODScheme2HistoricStage(ZdString *sExtensionRecode,
                                                 ZdString *sNodesRecode,
                                                 bitset<MAXNUMPATIENTRECORDS> &bsRecordsWithUnrecodedFields,
                                                 long lRecordNumber)
{
   try
      {
      ZdString sSiteNumeric  = gvFieldValues[2].GetSubstring(1);
      ZdString sHistology    = gvFieldValues[3];
      ZdString sOld13Digit   = gvFieldValues[11];
      ZdString sOld13Digit01 = sOld13Digit.GetSubstring(0,1);
      ZdString sOld13Digit02 = sOld13Digit.GetSubstring(1,1);
      ZdString sOld13Digit04 = sOld13Digit.GetSubstring(3,1);
      ZdString sOld13Digit05 = sOld13Digit.GetSubstring(4,1);
      ZdString sOld13Digit06 = sOld13Digit.GetSubstring(5,1);
      ZdString sOld13Digit07 = sOld13Digit.GetSubstring(6,1);
      ZdString sOld13Digit08 = sOld13Digit.GetSubstring(7,1);
      ZdString sOld13Digit09 = sOld13Digit.GetSubstring(8,1);
      ZdString sOld13Digit10 = sOld13Digit.GetSubstring(9,1);
      ZdString sOld13Digit11 = sOld13Digit.GetSubstring(10,1);
      ZdString sOld13Digit12 = sOld13Digit.GetSubstring(11,1);
      ZdString sOld13Digit13 = sOld13Digit.GetSubstring(12,1);
      ZdVector< ZdString >	vDummyVector(1);
      short wSite = atoi(sSiteNumeric.GetCString());
      short wHistology = atoi(sHistology.GetCString());

      if (8000 <= wHistology && wHistology <= 9589)
         {
         try
            {
   	      ZdVector< ZdPolymorph<ZdTLTLookupKey> > vEOD2NodesRecodeTableQuery( pEOD2NodesRecodeTable->GetQueryTemplate() );
            vEOD2NodesRecodeTableQuery.GetElement(0)->SetValue(sSiteNumeric);
            vEOD2NodesRecodeTableQuery.GetElement(1)->SetValue(sOld13Digit07);
            vEOD2NodesRecodeTableQuery.GetElement(2)->SetValue(sOld13Digit08);
            vEOD2NodesRecodeTableQuery.GetElement(3)->SetValue(sOld13Digit09);
            vEOD2NodesRecodeTableQuery.GetElement(4)->SetValue(sOld13Digit10);
            vEOD2NodesRecodeTableQuery.GetElement(5)->SetValue(sOld13Digit11);
            vEOD2NodesRecodeTableQuery.GetElement(6)->SetValue(sOld13Digit12);
            (*sNodesRecode) = pEOD2NodesRecodeTable->GetResult(vEOD2NodesRecodeTableQuery, vDummyVector).GetElement(0);
            }
         catch (ZdException &x)
            {
            (*sNodesRecode) = "99";
            }
         try
            {
         	ZdVector< ZdPolymorph<ZdTLTLookupKey> > vEOD2ExtensionRecodeTableQuery( pEOD2ExtensionRecodeTable->GetQueryTemplate() );
            vEOD2ExtensionRecodeTableQuery.GetElement(0)->SetValue(sSiteNumeric);
            vEOD2ExtensionRecodeTableQuery.GetElement(1)->SetValue(sOld13Digit04);
            vEOD2ExtensionRecodeTableQuery.GetElement(2)->SetValue(sOld13Digit05);
            vEOD2ExtensionRecodeTableQuery.GetElement(3)->SetValue(sOld13Digit06);
            vEOD2ExtensionRecodeTableQuery.GetElement(4)->SetValue(sOld13Digit07);
            vEOD2ExtensionRecodeTableQuery.GetElement(5)->SetValue(sOld13Digit08);
            vEOD2ExtensionRecodeTableQuery.GetElement(6)->SetValue(sOld13Digit13);
            (*sExtensionRecode) = pEOD2ExtensionRecodeTable->GetResult(vEOD2ExtensionRecodeTableQuery, vDummyVector).GetElement(0);
            }
         catch (ZdException &x)
            {
            (*sExtensionRecode) = "99";
            }
         if (IsLung(sSiteNumeric))
            {
            try
               {
      	      ZdVector< ZdPolymorph<ZdTLTLookupKey> > vEOD2HistoricStageLungTableQuery( pEOD2HistoricStageLungTable->GetQueryTemplate() );
               vEOD2HistoricStageLungTableQuery.GetElement(0)->SetValue(sOld13Digit04);
               vEOD2HistoricStageLungTableQuery.GetElement(1)->SetValue(sOld13Digit05);
               vEOD2HistoricStageLungTableQuery.GetElement(2)->SetValue(sOld13Digit06);
               vEOD2HistoricStageLungTableQuery.GetElement(3)->SetValue(sOld13Digit07);
               vEOD2HistoricStageLungTableQuery.GetElement(4)->SetValue(sOld13Digit08);
               vEOD2HistoricStageLungTableQuery.GetElement(5)->SetValue(sOld13Digit09);
               vEOD2HistoricStageLungTableQuery.GetElement(6)->SetValue(sOld13Digit10);
               vEOD2HistoricStageLungTableQuery.GetElement(7)->SetValue(sOld13Digit11);
               vEOD2HistoricStageLungTableQuery.GetElement(8)->SetValue(sOld13Digit12);
               vEOD2HistoricStageLungTableQuery.GetElement(9)->SetValue(sOld13Digit13);
               pEOD2HistoricStageLungTable->GetResult(vEOD2HistoricStageLungTableQuery, vDummyVector);
               gvRecodeValues[HISTORIC2000] = vDummyVector[0];
               }
            catch(ZdException &x)
               {
               gvRecodeValues[HISTORIC2000] = "99";
               gvlCasesUnableToBeRecoded[HISTORIC2000]++;
               bsRecordsWithUnrecodedFields.set(lRecordNumber);
               }
            }
         else if ( ( (440 <= wSite && wSite <= 447)
                     || IsVulva(sSiteNumeric)
                     || (600 <= wSite && wSite <= 609) )
                   && IsMaligMelanoma(sHistology) )
            {
            try
               {
      	      ZdVector< ZdPolymorph<ZdTLTLookupKey> > vEOD2HistoricStageMelanomaTableQuery( pEOD2HistoricStageMelanomaTable->GetQueryTemplate() );
               vEOD2HistoricStageMelanomaTableQuery.GetElement(0)->SetValue(sSiteNumeric);
               vEOD2HistoricStageMelanomaTableQuery.GetElement(1)->SetValue(sOld13Digit05);
               vEOD2HistoricStageMelanomaTableQuery.GetElement(2)->SetValue(sOld13Digit06);
               vEOD2HistoricStageMelanomaTableQuery.GetElement(3)->SetValue(sOld13Digit09);
               vEOD2HistoricStageMelanomaTableQuery.GetElement(4)->SetValue(sOld13Digit10);
               vEOD2HistoricStageMelanomaTableQuery.GetElement(5)->SetValue(sOld13Digit11);
               vEOD2HistoricStageMelanomaTableQuery.GetElement(6)->SetValue(sOld13Digit12);
               vEOD2HistoricStageMelanomaTableQuery.GetElement(7)->SetValue(sOld13Digit13);
               pEOD2HistoricStageMelanomaTable->GetResult(vEOD2HistoricStageMelanomaTableQuery, vDummyVector);
               gvRecodeValues[HISTORIC2000] = vDummyVector[0];
               }
            catch(ZdException &x)
               {
               gvRecodeValues[HISTORIC2000] = "99";
               gvlCasesUnableToBeRecoded[HISTORIC2000]++;
               bsRecordsWithUnrecodedFields.set(lRecordNumber);
               }
            }
         else if (IsBladder(sSiteNumeric))
            {
            try
               {
      	      ZdVector< ZdPolymorph<ZdTLTLookupKey> > vEOD2HistoricStageBladderTableQuery( pEOD2HistoricStageBladderTable->GetQueryTemplate() );
               vEOD2HistoricStageBladderTableQuery.GetElement(0)->SetValue(sOld13Digit05);
               vEOD2HistoricStageBladderTableQuery.GetElement(1)->SetValue(sOld13Digit06);
               vEOD2HistoricStageBladderTableQuery.GetElement(2)->SetValue(sOld13Digit07);
               vEOD2HistoricStageBladderTableQuery.GetElement(3)->SetValue(sOld13Digit09);
               vEOD2HistoricStageBladderTableQuery.GetElement(4)->SetValue(sOld13Digit10);
               vEOD2HistoricStageBladderTableQuery.GetElement(5)->SetValue(sOld13Digit11);
               vEOD2HistoricStageBladderTableQuery.GetElement(6)->SetValue(sOld13Digit12);
               vEOD2HistoricStageBladderTableQuery.GetElement(7)->SetValue(sOld13Digit13);
               pEOD2HistoricStageBladderTable->GetResult(vEOD2HistoricStageBladderTableQuery, vDummyVector);
               gvRecodeValues[HISTORIC2000] = vDummyVector[0];
               }
            catch(ZdException &x)
               {
               gvRecodeValues[HISTORIC2000] = "99";
               gvlCasesUnableToBeRecoded[HISTORIC2000]++;
               bsRecordsWithUnrecodedFields.set(lRecordNumber);
               }
            }
         else
            {
            try
               {
      	      ZdVector< ZdPolymorph<ZdTLTLookupKey> > vEOD2HistoricStageFromExtensionAndNodesTableQuery(
                  pEOD2HistoricStageFromExtensionAndNodesTable->GetQueryTemplate() );
               vEOD2HistoricStageFromExtensionAndNodesTableQuery.GetElement(0)->SetValue(sSiteNumeric);
               vEOD2HistoricStageFromExtensionAndNodesTableQuery.GetElement(1)->SetValue(sExtensionRecode->GetCString());
               vEOD2HistoricStageFromExtensionAndNodesTableQuery.GetElement(2)->SetValue(sNodesRecode->GetCString());
               pEOD2HistoricStageFromExtensionAndNodesTable->GetResult(vEOD2HistoricStageFromExtensionAndNodesTableQuery, vDummyVector);
               gvRecodeValues[HISTORIC2000] = vDummyVector[0];
               }
            catch(ZdException &x)
               {
               gvRecodeValues[HISTORIC2000] = "99";
               gvlCasesUnableToBeRecoded[HISTORIC2000]++;
               bsRecordsWithUnrecodedFields.set(lRecordNumber);
               }
            }
         }
      else if (9590 <= wHistology && wHistology <= 9979)
         {
         gvRecodeValues[HISTORIC2000] = "95";  // Unstaged lymph/leuk
         }
      else
         {
         gvRecodeValues[HISTORIC2000] = "99";
         gvlCasesUnableToBeRecoded[HISTORIC2000]++;
         bsRecordsWithUnrecodedFields.set(lRecordNumber);
         }
      }
   catch (ZdException &x)
      {
      x.AddCallpath("RecodeEODScheme2HistoricStage()", "StageRecodes");
      throw;
      }
}

//---------------------------------------------------------------------------
void StageRecodes::RecodeEODScheme3HistoricStage(ZdString *sExtensionRecode,
                                                 ZdString *sNodesRecode,
                                                 bitset<MAXNUMPATIENTRECORDS> &bsRecordsWithUnrecodedFields,
                                                 long lRecordNumber)
{
   try
      {
      ZdString sReportingSource  = gvFieldValues[0];
      ZdString sSiteNumeric      = gvFieldValues[2].GetSubstring(1);
      ZdString sHistology        = gvFieldValues[3];
      ZdString sEODCodingSystem  = gvFieldValues[7];
      ZdString sOld4DigitExtent  = gvFieldValues[8];
      ZdString sOld4DigitNodes   = gvFieldValues[9];
      ZdString sOld4DigitSize    = gvFieldValues[10];


      if(IsInteger(sOld4DigitExtent))
         (*sExtensionRecode) = sOld4DigitExtent;
      else
         (*sExtensionRecode) = "99";

      if(IsInteger(sOld4DigitNodes))
         (*sNodesRecode) = sOld4DigitNodes;
      else
         (*sNodesRecode) = "99";

      try
         {
	      ZdVector< ZdPolymorph<ZdTLTLookupKey> > vEOD3HistoricStageTableQuery( pEOD3HistoricStageTable->GetQueryTemplate() );
         ZdVector< ZdString >	vDummyVector(1);
         vEOD3HistoricStageTableQuery.GetElement(0)->SetValue(sHistology);
         vEOD3HistoricStageTableQuery.GetElement(1)->SetValue(sSiteNumeric);
         vEOD3HistoricStageTableQuery.GetElement(2)->SetValue(sExtensionRecode->GetCString());
         vEOD3HistoricStageTableQuery.GetElement(3)->SetValue(sNodesRecode->GetCString());
         pEOD3HistoricStageTable->GetResult(vEOD3HistoricStageTableQuery, vDummyVector);
         gvRecodeValues[HISTORIC2000] = vDummyVector[0];
         }
      catch (ZdException &x)
         {
         gvRecodeValues[HISTORIC2000] = "99";
         gvlCasesUnableToBeRecoded[HISTORIC2000]++;
         bsRecordsWithUnrecodedFields.set(lRecordNumber);
         }

      // Special recodes from Lynn Ries
      short wHistology = atoi(sHistology.GetCString());
      if ( sSiteNumeric == "569" && sOld4DigitSize == "02" && atoi(sExtensionRecode->GetCString()) > 0 )
         {
         gvRecodeValues[HISTORIC2000] = "40";
         } 
/*** Deleted 3/31/2006 to make all lymphoma stage ==9         
      else if (9590 <= wHistology && wHistology <= 9717) // Lymphoma
         {
         if ( (*sExtensionRecode) == "9" || sReportingSource == "7" )
            {
            gvRecodeValues[HISTORIC2000] = "90";
            }
         else if ( (*sExtensionRecode) == "1")
            {
            gvRecodeValues[HISTORIC2000] = "10";
            }
         else if ( (*sExtensionRecode) ==  "2")
            {
            gvRecodeValues[HISTORIC2000] = "20";
            }
         else if ( (*sExtensionRecode) == "3" || (*sExtensionRecode) == "8")
            {
            gvRecodeValues[HISTORIC2000] = "40";
            }
         }*/
      // end of Special recodes from Lynn Ries
      }
   catch (ZdException &x)
      {
      x.AddCallpath("RecodeEODScheme3Stages()", "StageRecodes");
      throw;
      }
}

//---------------------------------------------------------------------------
void StageRecodes::RecodeEODScheme4Stages(bitset<MAXNUMPATIENTRECORDS> &bsRecordsWithUnrecodedFields,
                                          long lRecordNumber)
{
   short wYearDx;
   int   iSite,
         iHistology;
   try
      {
      ZdString sReportingSource  = gvFieldValues[0];
      ZdString sYearDx           = gvFieldValues[1];
      ZdString sSiteNumeric      = gvFieldValues[2].GetSubstring(1);
      ZdString sHistology        = gvFieldValues[3];
      ZdString sExtension        = gvFieldValues[4];
      ZdString sNodes            = gvFieldValues[5];
      ZdString sProstatePathExt  = gvFieldValues[6];
      ZdString sRegistry         = gvFieldValues[13];
      ZdString sBehavior         = gvFieldValues[14];
      if (sBehavior == "1")
         sBehavior == "3";

      wYearDx     = atoi(sYearDx.GetCString());
      iSite       = atoi(sSiteNumeric);
      iHistology  = atoi(sHistology);

      // from 1988 to 1991, LA registry did not provide EOD codes
      // those cases should not be coded to error, but left blank
      // the COBOL recoded these cases to error
      if (atoi(sRegistry.GetCString()) == 1535 && 1988 <= wYearDx && wYearDx <= 1991)
         { 
         gvRecodeValues[SSG2000]       = "";
         gvRecodeValues[SSG77]         = "";
         gvRecodeValues[HISTORIC2000]  = "";
         }
      else if (!IsInteger(sYearDx) ||
          !IsInteger(sExtension) ||
          !IsInteger(sNodes))
         {
         gvRecodeValues[SSG2000]       = "9";
         gvRecodeValues[SSG77]         = "9";
         gvRecodeValues[HISTORIC2000]  = "99";
         gvlCasesUnableToBeRecoded[SSG2000]++;
         gvlCasesUnableToBeRecoded[SSG77]++;
         gvlCasesUnableToBeRecoded[HISTORIC2000]++;
         bsRecordsWithUnrecodedFields.set(lRecordNumber);
         }
      else if (sReportingSource == "7")
         {
         		gvRecodeValues[SSG2000]          = "9";
         		gvRecodeValues[SSG77]            = "9";
         		gvRecodeValues[HISTORIC2000]     = "90";
         		gvRecodeValues[PSTSSG2000]       = "9";
         		gvRecodeValues[PSTSSG77]         = "9";
         		gvRecodeValues[PSTHISTORIC2000]  = "90"; 
         		
      	 		// Quick fix: Certain cases are not consistent with old codes, leave them blank
      	 		if ((8000 <= iHistology && iHistology <= 9139) || (9141 <= iHistology && iHistology <= 9589))
      	 		   {
      	 		   if ((((79 <= iSite && iSite <= 81) || (88 <= iSite && iSite <=89 )) && (1984 <= wYearDx && wYearDx <= 1997)) ||
      	 		       ((90 <= iSite && iSite <= 109) && (1984 <= wYearDx && wYearDx <= 1988)) ||
      	 		       ((iSite == 619) && (1984 <= wYearDx && wYearDx <= 1997))) 
      	 		      	gvRecodeValues[SSG2000] = " ";
      	 		   }
         		
 	     	 		if (((8000 <= iHistology && iHistology <= 9139) || (9141 <= iHistology && iHistology <= 9589)) && iSite == 619 && wYearDx < 1995)
 	     	 			gvRecodeValues[SSG77] = " ";
 	     	 			
 	     	 		//Special Leukemia case
 	     	 		if (gbIsLeuk)	gvRecodeValues[HISTORIC2000] = "40";
         }
      else
         {
         ZdVector<ZdString>          vExtensionRecodes(3),
                                     vNodeRecodes(3);

         try
            {
            // Extension Recode table lookup
            ZdVector< ZdPolymorph<ZdTLTLookupKey> > vExtRecodeTableQuery(pExtRecodeTable->GetQueryTemplate());
            vExtRecodeTableQuery.GetElement(0)->SetValue(sYearDx);
            vExtRecodeTableQuery.GetElement(1)->SetValue(sSiteNumeric);
            vExtRecodeTableQuery.GetElement(2)->SetValue(sHistology);
            vExtRecodeTableQuery.GetElement(3)->SetValue(sExtension);
            pExtRecodeTable->GetResult(vExtRecodeTableQuery, vExtensionRecodes);
            }
         catch (ZdException &x)
            {
            // do nothing if the codes are not found
            }

         // Breast cases sometimes depend on Behavior; this is a quick fix
         if (IsBreast(sSiteNumeric) && sExtension == "05")
            {
            if (sBehavior == "2")
               {
               vExtensionRecodes[0] = "1";
               vExtensionRecodes[1] = "1";
               vExtensionRecodes[2] = "1";
               }
            else //sBehavior == "3"
               {
               vExtensionRecodes[0] = "2";
               vExtensionRecodes[1] = "2";
               vExtensionRecodes[2] = "2";
               }
            }
         try     // if it is a histology-based recode
            {
            // Histology Node Recode Table lookup
            ZdVector< ZdPolymorph<ZdTLTLookupKey> > vHistNodeRecodeTableQuery(pHistNodeRecodeTable->GetQueryTemplate());
            vHistNodeRecodeTableQuery.GetElement(0)->SetValue(sSiteNumeric);
            vHistNodeRecodeTableQuery.GetElement(1)->SetValue(sHistology);
            vHistNodeRecodeTableQuery.GetElement(2)->SetValue(sNodes);
            pHistNodeRecodeTable->GetResult(vHistNodeRecodeTableQuery, vNodeRecodes);
            }
         catch (ZdException &x)
            {
            }

         // This is the special case for melanoma
         if (((440 <= iSite && iSite <= 449) ||
             (510 <= iSite && iSite <= 519) ||
             (600 <= iSite && iSite <= 602) ||
             (608 <= iSite && iSite <= 609) ||
             (iSite == 632)) &&
             8720 <= iHistology && iHistology <= 8790 &&
             sNodes == "3" && sExtension == "99" )
            {
            vExtensionRecodes[1] = "3";
            }

         FindAndStore2000VersionRecodes(bsRecordsWithUnrecodedFields, lRecordNumber); 
         
         }
         
      }
   catch (ZdException &x)
      {
      x.AddCallpath("RecodeEODScheme4Stages()", "StageRecodes");
      throw;
      }
}




//---------------------------------------------------------------------------
void StageRecodes::Setup(ZdIniSection *pSection, ZdProgressInterface &theProgressInterface)
{
   ZdIniFile                  *pTempIniFile = 0;
   try
      {
      ZdSubProgress               theSubProgress(theProgressInterface);
      // get the table ini file
      pTempIniFile = new ZdIniFile(pSection->GetString("TableFile"));
      if (!pTempIniFile)
         {
         ZdString sError;
         sError << ZdString::reset << "No Table File Specified for"
                << pSection->GetName() << " Recode - Setup()";
         ZdGenerateException(sError.GetCString(), "StageRecodes");
         }
      theProgressInterface.SetRange(0,320);
      // Build all the tables
      theProgressInterface.SetSubrange(0,10);
      pEOD0HistoricStageTable =
         CreateTable("EOD0 Historic Stage", pTempIniFile, theSubProgress);
      theProgressInterface.SetSubrange(10,20);
      pEOD1ExtensionRecodeTable =
         CreateTable("EOD1 Extension", pTempIniFile, theSubProgress);
      theProgressInterface.SetSubrange(20,30);
      pEOD1NodesRecodeTable =
         CreateTable("EOD1 Nodes", pTempIniFile, theSubProgress);
      theProgressInterface.SetSubrange(30,40);
      pEOD1HistoricStageDirectTable =
         CreateTable("EOD1 Historic Stage Direct", pTempIniFile, theSubProgress);
      theProgressInterface.SetSubrange(40,50);
      pEOD1HistoricStageFromExtensionAndNodesTable =
         CreateTable("EOD1 Historic Stage from Extension and Nodes", pTempIniFile, theSubProgress);
      theProgressInterface.SetSubrange(50,60);
      pEOD2ExtensionRecodeTable =
         CreateTable("EOD2 Extension", pTempIniFile, theSubProgress);
      theProgressInterface.SetSubrange(60,70);
      pEOD2NodesRecodeTable =
         CreateTable("EOD2 Nodes", pTempIniFile, theSubProgress);
      theProgressInterface.SetSubrange(70,80);
      pEOD2HistoricStageLungTable =
         CreateTable("EOD2 Historic Stage Lung", pTempIniFile, theSubProgress);
      theProgressInterface.SetSubrange(80,90);
      pEOD2HistoricStageMelanomaTable =
         CreateTable("EOD2 Historic Stage Melanoma", pTempIniFile, theSubProgress);
      theProgressInterface.SetSubrange(90,100);
      pEOD2HistoricStageBladderTable =
         CreateTable("EOD2 Historic Stage Bladder", pTempIniFile, theSubProgress);
      theProgressInterface.SetSubrange(100,110);
      pEOD2HistoricStageFromExtensionAndNodesTable =
         CreateTable("EOD2 Historic Stage from Extension and Nodes", pTempIniFile, theSubProgress);
      theProgressInterface.SetSubrange(110,120);
      pEOD3HistoricStageTable =
         CreateTable("EOD3 Historic Stage", pTempIniFile, theSubProgress);
      theProgressInterface.SetSubrange(120,130);
      pEOD0_3HistoricStagePatchTable =
         CreateTable("EOD 0-3 Historic Stage Patch Table", pTempIniFile, theSubProgress);
      theProgressInterface.SetSubrange(130,140);
      pHistNodeRecodeTable =
         CreateTable("LRD Histology Node Recode", pTempIniFile, theSubProgress);
      theProgressInterface.SetSubrange(140,150);
      pExtRecodeTable =
         CreateTable("LRD Extension Table", pTempIniFile, theSubProgress);
      theProgressInterface.SetSubrange(150,160);
      pStageRecodeTable =
         CreateTable("LRD Stage", pTempIniFile, theSubProgress);
      theProgressInterface.SetSubrange(160,170);
      pSummaryStage2000RecodeTable =
         CreateTable("Summary Stage 2000", pTempIniFile, theSubProgress);
      theProgressInterface.SetSubrange(170,180);
      pEOD0AJCCStageTable =
         CreateTable("EOD0 AJCC Stage", pTempIniFile, theSubProgress);
      theProgressInterface.SetSubrange(180,190);
      pEOD1_3AJCCStageTable =
         CreateTable("EOD1-3 AJCC Stage", pTempIniFile, theSubProgress);
      theProgressInterface.SetSubrange(190,200);
      pEOD2AJCCStageMelanomaTable =
         CreateTable("EOD2 AJCC Stage Melanoma", pTempIniFile, theSubProgress);
      theProgressInterface.SetSubrange(200,210);
      pEOD2AJCCStageBladderTable =
         CreateTable("EOD2 AJCC Stage Bladder", pTempIniFile, theSubProgress);
      theProgressInterface.SetSubrange(210,220);
      pEOD2AJCCStageFromExtensionAndNodesTable =
         CreateTable("EOD2 AJCC Stage from Extension and Nodes", pTempIniFile, theSubProgress);
      theProgressInterface.SetSubrange(220,230);
      pAJCCStageShouldBeCodedTable =
         CreateTable("AJCC Stage Should Be Coded", pTempIniFile, theSubProgress);
      theProgressInterface.SetSubrange(230,240);
      pEOD3AJCCStageBreastTable =
         CreateTable("EOD3 AJCC Stage Breast", pTempIniFile, theSubProgress);
      theProgressInterface.SetSubrange(240,250);
      pExt2000RecodeTable =
         CreateTable("New LRD Extension Table", pTempIniFile, theSubProgress);
      theProgressInterface.SetSubrange(250,260);
      pProstateExt2000RecodeTable =
         CreateTable("New LRD Prostate Extension Table", pTempIniFile, theSubProgress);
      theProgressInterface.SetSubrange(260,270);
      pNode2000RecodeTable =
         CreateTable("New LRD Nodes Table", pTempIniFile, theSubProgress);
      theProgressInterface.SetSubrange(270,280);
      pCSExtensionTable =
      	CreateTable("CS Extension", pTempIniFile, theSubProgress);
      theProgressInterface.SetSubrange(280,290);
      pCSNodeTable =
      	CreateTable("CS Lymph Nodes",pTempIniFile, theSubProgress);
      theProgressInterface.SetSubrange(290,300);
      pCSMetsTable =
      	CreateTable("CS Mets at Dx", pTempIniFile, theSubProgress);
      theProgressInterface.SetSubrange(300,310);
      pCSHistStageTable =
      	CreateTable("CS Historic Stage", pTempIniFile, theSubProgress);
      theProgressInterface.SetSubrange(310,320);
      pLeukemiaTable =
      	CreateTable("Leukemia Table", pTempIniFile, theSubProgress);
      

      delete pTempIniFile;          pTempIniFile = 0;
      }
   catch (ZdException &x)
      {
      delete pTempIniFile;          pTempIniFile = 0;
      x.AddCallpath("Setup()", "StageRecodes");
      throw;
      }
}

//---------------------------------------------------------------------------

