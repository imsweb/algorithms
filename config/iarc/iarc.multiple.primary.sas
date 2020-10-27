filename '<removed>';
filename out '<removed>';
filename iarcout '<removed>';

proc format; 
	value iarc 
	  1 = 'Primary'
	  0 = 'Duplicate'; 

%macro hemat(num);
data hemat&num.; 
	set mult; 
	if histgrp=&num; 
	
proc sort; by registry casenum histgrp seqnum descending hist3;	  
	
data hemat&num. hematout&num.; 
	set hemat&num.; 
	  by registry casenum histgrp; 
  if first.histgrp then output hemat&num.;
  else output hematout&num.;

%mend;  

data seer; 
  infile seer lrecl=3330; 
  input @42 casenum $char8. 
        @38 registry $char2.
        @192 sex $char1. 
	      @193 age $char3. 
	      @196 birth_yr $char4.
	      @200 birth_mo $char2.
	      @528 seqnum $Char2.
	      @2322 dx_mo $char2. /*use recoded Month of Diagnosis */
	      @530 dx_yr $char4. 
	      @534 temp_dx_mo $char2.
	      @540 site $char4.
	      @540 site3 $char3.
	      @550 hist3 4. 
	      @554 beh3 $char1. 
	      @555 grade $char1.
	      @2833 pubid $char8. /*research id */
	      @562 dx_conf $char1. ;
	      
/*SEER does not require day of diagnosis.  This day of diagnosis variable was created using the sequence number to sequence cases diagnosed in the same month
according to their sequence number.  The IARC does not take sequence number into account when sorting the tumor records so this solution allowed the cases to be
sorted properly based on sequence number */	      
  if dx_yr = '2016' and dx_mo = '  ' then dx_mo = temp_dx_mo; 
  if seqnum in (0,60) then dx_dy = 1; 
  else if 61 <= seqnum <=88 then dx_dy = seqnum - 60;
  else dx_dy = seqnum*1;
  if dx_dy > 28 then dx_dy=28; 	      
  birth_dy = '01';
  dxdate = mdy(dx_mo,dx_dy,dx_yr);
    
proc sort; by registry casenum dxdate;  
	
data check notmalig ; 
	set seer; 
	  by registry casenum dxdate;
  if beh3 = '2' and not ('C670' <= site <= 'C679') then output notmalig ; /*Removes in situ cases that are not bladder */
  else if beh3 in ('0','1') and not ('C670' <= site <= 'C679') then output notmalig; /*Removes all benign/borderline cases */
  else output check; /*Contains cases that would be processed by the IARC program*/
  
data notmalig ;
	set notmalig; 
	newiarc = 9;
  
proc sort; by registry casenum dxdate; 

/*Separates cases into those with only one tumor record in the file and those with more than one record.  Those with only one record are automatically set to 1.*/	
data single mult; 
	set check; 
	  by registry casenum dxdate; 
  if (first.casenum and last.casenum) then do; 
  	newiarc = 1;
  	output single; 
  end;
  else output mult; 	  
  
  
/*These histology groups and site groups are those that are defined by IARC*/  
data mult; 
	set mult;  
	 newiarc=0;
     
   if 8051 <= hist3 <= 8084 or 8120 <= hist3 <= 8131 then histgrp = 1;
   else if 8090 <= hist3 <= 8110 then histgrp = 2; 
   else if 8140 <= hist3 <= 8149 or 8160 <= hist3 <= 8162 or 8190 <= hist3 <= 8221 or 8260 <= hist3 <= 8337 or 8350 <= hist3 <= 8551 or 8570 <= hist3 <= 8576 or 8940 <= hist3 <= 8941 then histgrp =3;
   else if 8030 <= hist3 <= 8046 or 8150 <= hist3 <= 8157 or 8170 <= hist3 <= 8180 or 8230 <= hist3 <= 8255 or 8340 <= hist3 <= 8347 or 8560 <= hist3 <= 8562 or 8580 <= hist3 <= 8671 then histgrp = 4;
   else if 8010 <= hist3 <= 8015 or 8020 <= hist3 <= 8022 or hist3 = 8050 then histgrp = 5; 
   else if 8680 <= hist3 <= 8713 or 8800 <= hist3 <= 8921 or 8990 <= hist3 <= 8991 or 9040 <= hist3 <= 9044 or 9120 <= hist3 <= 9125 or 9130 <= hist3 <= 9136 or 9141 <= hist3 <= 9252 or 
           9370 <= hist3 <= 9373 or 9540 <= hist3 <= 9582 then histgrp = 6; 
   else if 9050 <= hist3 <= 9055 then histgrp = 7; 
   else if hist3 =9840 or 9861 <= hist3 <= 9931 or 9945 <= hist3 <= 9946 or hist3 = 9950 or 9961 <= hist3 <= 9967 or 9980 <= hist3 <= 9987 or hist3 in (9991,9992) then histgrp = 8; 
   else if 9670 <= hist3 <= 9699 or 9731 <= hist3 <= 9738 or 9761 <= hist3 <= 9767 or 9811 <= hist3 <= 9818 or 9823 <= hist3 <= 9826 or hist3 in 
     (9597,9712,9728,9769,9833,9836,9940) then histgrp = 9;
   else if 9700 <= hist3 <= 9724 or hist3 in (9726,9729,9768) or 9827 <= hist3 <= 9831 or hist3 in (9834,9837,9948) then histgrp = 10;
   else if 9650 <= hist3 <= 9667 then histgrp = 11; 
   else if 9740 <= hist3 <= 9742 then histgrp = 12; 
   else if 9750 <= hist3 <= 9759 then histgrp = 13; 
   else if 9590 <= hist3 <= 9591 or hist3 in (9596,9725,9727,9760) or 9800 <= hist3 <= 9809 or hist3 in (9820,9832,9835,9860,9960) or 9965 <= hist3 <= 9975 or hist3 in (9989) then histgrp = 14; 
   else if hist3 = 9140 then histgrp = 15; 
   else if 8720 <= hist3 <= 8790 or 8930 <= hist3 <= 8936 or 8950 <= hist3 <= 8983 or 9000 <= hist3 <= 9030 or 9060 <= hist3 <= 9110 or 9260 <= hist3 <= 9365 or 9380 <= hist3 <= 9539 then histgrp = 16;
   else if 8000 <= hist3 <= 8005 then histgrp = 17;  
   
   if 8051 <= hist3 <= 8084 or 8120 <= hist3 <= 8131 then ohistgrp = 1;
   else if 8090 <= hist3 <= 8110 then ohistgrp = 2; 
   else if 8140 <= hist3 <= 8149 or 8160 <= hist3 <= 8162 or 8190 <= hist3 <= 8221 or 8260 <= hist3 <= 8337 or 8350 <= hist3 <= 8551 or 8570 <= hist3 <= 8576 or 8940 <= hist3 <= 8941 then ohistgrp =3;
   else if 8030 <= hist3 <= 8046 or 8150 <= hist3 <= 8157 or 8170 <= hist3 <= 8180 or 8230 <= hist3 <= 8255 or 8340 <= hist3 <= 8347 or 8560 <= hist3 <= 8562 or 8580 <= hist3 <= 8671 then ohistgrp = 4;
   else if 8010 <= hist3 <= 8015 or 8020 <= hist3 <= 8022 or hist3 = 8050 then ohistgrp = 5; 
   else if 8680 <= hist3 <= 8713 or 8800 <= hist3 <= 8921 or 8990 <= hist3 <= 8991 or 9040 <= hist3 <= 9044 or 9120 <= hist3 <= 9125 or 9130 <= hist3 <= 9136 or 9141 <= hist3 <= 9252 or 
           9370 <= hist3 <= 9373 or 9540 <= hist3 <= 9582 then ohistgrp = 6; 
   else if 9050 <= hist3 <= 9055 then ohistgrp = 7; 
   else if hist3 =9840 or 9861 <= hist3 <= 9931 or 9945 <= hist3 <= 9946 or hist3 = 9950 or 9961 <= hist3 <= 9964 or 9980 <= hist3 <= 9987 then ohistgrp = 8; 
   else if 9670 <= hist3 <= 9699 or hist3 = 9728 or 9731 <= hist3 <= 9734 or 9761 <= hist3 <= 9767 or hist3 = 9769 or 9823 <= hist3 <= 9826 or hist3 in 
     (9833,9836,9940) then ohistgrp = 9;
   else if 9700 <= hist3 <= 9719 or hist3 in (9729,9768) or 9827 <= hist3 <= 9831 or hist3 in (9834,9837,9948) then ohistgrp = 10;
   else if 9650 <= hist3 <= 9667 then ohistgrp = 11; 
   else if 9740 <= hist3 <= 9742 then ohistgrp = 12; 
   else if 9750 <= hist3 <= 9758 then ohistgrp = 13; 
   else if 9590 <= hist3 <= 9591 or hist3 in (9596,9727,9760) or 9800 <= hist3 <= 9801 or hist3 in (9805,9820,9832,9835,9860,9960,9970,9975,9989) then ohistgrp = 14; 
   else if hist3 = 9140 then ohistgrp = 15; 
   else if 8720 <= hist3 <= 8790 or 8930 <= hist3 <= 8936 or 8950 <= hist3 <= 8983 or 9000 <= hist3 <= 9030 or 9060 <= hist3 <= 9110 or 9260 <= hist3 <= 9365 or 9380 <= hist3 <= 9539 then ohistgrp = 16;
   else if 8000 <= hist3 <= 8005 then ohistgrp = 17;  
   
   if site3 in ('C01','C02') then sitegrp = 'C02 ';
   else if site3 in ('C00','C03','C04','C05','C06') then sitegrp = 'C06 ';
   else if site3 in ('C09','C10','C12','C13','C14') then sitegrp = 'C14 ';
   else if site3 in ('C19','C20') then sitegrp = 'C20 ';
   else if site3 in ('C23', 'C24') then sitegrp = 'C24 ';
   else if site3 in ('C33', 'C34') then sitegrp = 'C34 ';
   else if site3 in ('C40', 'C41') then sitegrp = 'C41 ';
   else if site3 in ('C65', 'C66', 'C67','C68') then sitegrp = 'C68 ';
   else sitegrp = site3;
   
    
   
proc sort data=mult;
  by registry casenum dxdate seqnum histgrp; 
  run;


%hemat(8);
%hemat(9);
%hemat(10);
%hemat(11);
%hemat(12);
%hemat(13);
%hemat(14);

data hemat;
	set hemat8 hemat9 hemat10 hemat11 hemat12 hemat13 hemat14; 
	
proc sort; by registry casenum histgrp dxdate seqnum; 
	
data hemat hemat_mult; 
	set hemat;
	by registry casenum histgrp dxdate seqnum; 
	if (first.casenum and last.casenum) then do; 
		 newiarc = 1;
		 output	hemat;
		end;
	else output hemat_mult; 
	
proc sort data=hemat_mult; by registry casenum descending histgrp dxdate seqnum;	
	
data hemat_mult; 
	set hemat_mult;
	  by registry casenum descending histgrp dxdate seqnum; 
	retain unkspec ; 
	if first.casenum then do; 
		if histgrp = 14 then unkspec = 1; 
		else unkspec = 0; 
	end;
	
data hemat2 hematunk;
	set hemat_mult; 
	if unkspec = 0 then do; 
		newiarc = 1; 
		output hemat2; 
	end;
  else if unkspec = 1 then output hematunk; 
  
proc sort data=hematunk; by registry casenum dxdate seqnum;
	
proc freq data=hematunk noprint;
	tables registry*casenum/out=numrecs; 
	
data hematunk hematunk2; 
	merge hematunk numrecs;
	  by registry casenum ; 
  if count = 2 then output hematunk;
  else output hematunk2;	  
  
proc sort data=hematunk; by registry casenum dxdate seqnum;
	
data hematunk hematdup; 
	set hematunk;
	  by registry casenum dxdate seqnum; 	  
  if first.casenum then do; 
  	newiarc = 1;
  	output hematunk;
  end;
  else output hematdup;
  
proc sort data=hematunk2; by registry casenum dxdate seqnum;  
	
	
data hematunk2 hematdup2;
	set hematunk2; 
	  by registry casenum dxdate seqnum;   
  retain foundh;	  
  if first.casenum then do; 
  	if histgrp = 14 then foundh = 1;
  	else foundh = 0;
  	newiarc = 1;
  	output hematunk2; 
  end;
  else if histgrp ne 14 and foundh = 1 then do; 
  	foundh = 0; 
  	output hematdup2;
  end; 
  else if histgrp = 14 then output hematdup2;
  else if histgrp ne 14 and foundh ne 1 then do; 
  	newiarc = 1;
  	output hematunk2;
  end;
  
data hemat; 
	set hemat hemat2 hematunk hematdup hematout8 hematout9 hematout10 hematout11 hematout12 hematout13 hematout14 hematunk2 hematdup2; 
		
data kaposi; 
  set mult; 
  if histgrp = 15; 

proc sort data=kaposi; 
  by registry casenum dxdate seqnum;
  
data kaposi dup1; 
  set kaposi; 
    by registry casenum dxdate seqnum; 
  if first.casenum then do;
   	  newiarc = 1;
  	  output kaposi; 
  	end;
  else output dup1;   	
	

data rest; 
  set mult; 
  if 8 <= histgrp <= 15 then delete;
  

proc sort; by registry casenum sitegrp histgrp dxdate seqnum ; 

data out1 single2; 
  set rest; 
    by registry casenum sitegrp histgrp dxdate seqnum ; 
  if (first.sitegrp and last.sitegrp) then do; 
  	newiarc=1;
  	output single2;
  end;
  else output out1;
  

proc sort data=out1; by registry casenum sitegrp descending histgrp; 
	
data carc other; 
	set out1; 
	if 1 <= histgrp <= 5 or histgrp = 17 then output carc; 
	else output other;
	
data carc; 
	set carc; 
	if histgrp = 17 then temp_histgrp = 5; 
	else temp_histgrp = histgrp; 	
	
proc sort data=carc; by registry casenum sitegrp descending temp_histgrp;

data carc;
	set carc;
	  by registry casenum sitegrp descending temp_histgrp;
  retain unkcarc; 
  if first.sitegrp then do; 
  	if temp_histgrp = 5 then unkcarc = 1; 
  	else unkcarc = 0;
  end;
  
data unkcarc restcarc; 
	set carc;  
	if unkcarc = 1 then output unkcarc; 
	else output restcarc;

proc sort data=unkcarc; by registry casenum sitegrp dxdate seqnum; 
	
proc freq data=unkcarc noprint;
	tables registry*casenum*sitegrp/out=numrecs; 
	
data unkcarc unkcarc2; 
	merge unkcarc numrecs;
	  by registry casenum sitegrp; 
  if count = 2 then output unkcarc;
  else output unkcarc2;	  	
  
data unkcarc ; 
	set unkcarc;
	  by registry casenum sitegrp dxdate ; 	  
  if first.sitegrp then newiarc = 1;
  	  
	
data unkcarc3 single3 unkcarc_dup1;
	set unkcarc2;
	  by registry casenum sitegrp dxdate seqnum; 
  retain found; 	  
  if first.sitegrp then do; 
  	if temp_histgrp = 5 then found = 1;
  	else found = 0;
  	newiarc = 1;
  	output single3; 
  end; 
  else if temp_histgrp ne 5 and found = 1 then output unkcarc3;
  else if temp_histgrp = 5 then output unkcarc_dup1;
  else if temp_histgrp ne 5 and found = 0 then output single3;
  
  
data unkcarc3 dupcarc ;
	set unkcarc3; 
	  by registry casenum sitegrp dxdate temp_histgrp ; 
  first_sitegrp = first.sitegrp; 
  last_sitegrp = last.sitegrp; 	  
  first_histgrp = first.temp_histgrp; 
  last_histgrp = last.temp_histgrp; 	  
  retain l_histgrp; 
  if first.sitegrp then do; 
  	l_histgrp = temp_histgrp; 
  	output dupcarc; 
  end;
  else do; 
  	if l_histgrp ne temp_histgrp then do; 
  	  newiarc = 1;
  	  output unkcarc3; 
  	end; 
    else output dupcarc; 
   end; 
 
data out1; 
	set single3 restcarc other;  
	
proc sort; by registry casenum sitegrp descending histgrp hist3;	

data out1; 
	set out1; 
	  by registry casenum sitegrp descending histgrp hist3;
	retain  unkspec; 
	if first.sitegrp then do; 
		if histgrp = 17 then unkspec = 1; 
		else unkspec = 0;
	end;
	
data unkspec out1; 
	set out1; 
	if unkspec = 1 then output unkspec; 
	else output out1;
		

proc sort data=unkspec; by registry casenum sitegrp dxdate descending hist3; 
	

data unkspec single3 ;
	set unkspec;
	  by registry casenum sitegrp dxdate descending hist3; 
  retain founds;	  
  if first.sitegrp then do; 
  	if histgrp = 17 then founds = 1;
  	else founds = 0;
  	newiarc = 1;
  	output single3; 
  end;
  else if histgrp ne 17 and founds = 1 then do; 
  	newiarc = 0;
  	output unkspec;
  end;
  else if histgrp = 17 then do; 
  	newiarc = 0;
  	output unkspec;
  end;
  else if histgrp ne 17 and founds ne 1 then do; 
  	newiarc = 1;
  	output single3;
  end;
  
  
proc sort data=single3; by registry casenum sitegrp histgrp dxdate descending hist3; 

data single3 dup3; 
	set single3;
	  by registry casenum sitegrp histgrp dxdate descending hist3; 
  if first.histgrp then output single3; 
  else do; 
  	newiarc =0; 
  	output dup3; 
  end; 
  	
		          
proc sort data=out1; by registry casenum sitegrp histgrp dxdate descending hist3;  
	
data single4 dup2; 
	set out1; 
	  by registry casenum sitegrp histgrp dxdate descending hist3 ;
  if first.histgrp then do; 
  	newiarc = 1; 
  	output single4; 
  end; 
  else output dup2;	  
	
data all; 
	set hemat kaposi dup1 unkspec single2 single3 single4 dup2 single dup3 notmalig unkcarc unkcarc3 dupcarc unkcarc_dup1;
	/*Ignore this logic - these are the handful of cases that I could not make match the IARC algorithm 
	if registry = '20' and casenum = '10989654' and seqnum = 2 then newiarc = 0; 
	if registry = '25' and casenum = '00287335' and seqnum = 4 then newiarc = 0; 
	if registry = '25' and casenum = '00704975' and seqnum = 3 then newiarc = 0; 
	if registry = '26' and casenum = '10012195' and seqnum = 2 then newiarc = 0; 
	if registry = '35' and casenum = '01023863' and seqnum = 5 then newiarc = 0; 
	if registry = '41' and casenum = '00269072' and seqnum = 4 then newiarc = 0;
	if registry = '42' and casenum = '00445538' and seqnum = 6 then newiarc = 0; */
	
       
proc sort; by registry casenum seqnum;

/*This creates a file that I can use as input to the IARCCrg Tools application to test their value against mine */
data _null_;
  set all;
  file out TERMSTR=CRLF; 
  put @1 registry $char2.
      @3 casenum $char8. 
      @11 seqnum $char2.
      @13 sex $char1.
      @14 age $char3. 
      @17 dx_mo $char2. 
      @19 dx_dy z2.
      @21 dx_yr $char4. 
      @25 site $char4.
      @29 hist3 4. 
      @33 beh3 $char1. 
      @34 grade $char1.
      @35 pubid $char8.
      @43 dx_conf $char1.
      @44 birth_mo $char2.
      @46 birth_dy $char2.
      @48 birth_yr $char4.
      @53 newiarc 1.
      @54 histgrp z2.
      @56 sitegrp $char3.;
  
      
data final; 
	set all ; 
	
proc sort; by registry casenum seqnum;
	
/*This creates the CSV file used to merge the value back into the SEER recoded files */
data _null_; 
	set final ; 
	file iarcout delimiter=',';
	put registry $ casenum $ seqnum $ newiarc; 
	
proc freq data=final; 
	tables newiarc;	
	
	




