*********************************************************************************************************************************************;
*** NAACCR Poverty Linkage Program - Version 4.0                                                                                             ;
/*SAS program to create percent poverty variable based upon county and census tract information in registry incidence data.
  The SAS Code in this program utilizes state, county, census tract information to generate a more precise value for Percent Poverty.
  The program will generate percent poverty as a grouped variable (4 categories).

  Name of file containing poverty group information by state, county, and census tract:
         poverty_format_lib.fmt

  NOTE: The following list identifies counties with possible geographic code errors or definition differences for tracts and block groups. Counties
        in this list may generate an "Unknown" poverty category group as a result.
         Alabama: Bibb
         Illinois: Coles
         Iowa: Adair, Adams, Cass, Clay, Dickinson, Dubuque, Emmet, Page, Sioux, Union, Worth
         Maryland: Calvert
         Massachusetts: Berkshire
         Michigan: Baraga
         Minnesota: Murray
         Texas: Wilson

         *For more information please see http://www.census.gov/acs/www/data_documentation/geography_notes/#tracts

  Written for NAACCR, Inc.
  August 2006
  For Holly Howe
  hhowe@naaccr.org  */
*********************************************************************************************************************************************;
**** Written/modified:                                                                                                                       ;
****    Andrew Lake, Information Management Services, Inc., 301.984.3445                                                                     ;
****    October 2006                                                                                                                         ;
****    August 2010 - Modified to accept either NAACCR version 11.3 or version 12.0 layout files                                             ;
****    September 2011 - Modified to accept only NAACCR version 12.0 layout files and use poverty2000 or acs file based on year of diagnosis ;
****    October 2013 - Modified to accept only NAACCR version 13.0 layout files                                                              ;
*********************************************************************************************************************************************;

****** census tract file;
filename census 'poverty_format_lib.fmt';
%include census;
*********************************************************************************************************************************************;
/***** NAACCR Version 13.0 input registry file, with the following fields included:
       Patient ID [item #20]
       State at DX [item #80]
       County at DX [item #90]
       Census Tract 2000 [item #130]
       Date of Diagnosis [item #390]****/;
*****filename regfile 'directory and file name';
filename regfile './sample.txt';

**** Output file Name - Text file, with Patient ID, state, county, census tract, census block, and generated Percent Poverty values;
*****filename outfile 'directory and file name' ;
filename outfile './out.txt';
*********************************************************************************************************************************************;

************************************************************************************************************************************************;
***** Read in data file, convert two letter state code to a 2 digit FIPS code, apply format to state/county/census tract, and write out records ;
************************************************************************************************************************************************;
data registry_data;
  /* read in cancer registry file with selected fields-based on NAACCR Version 13.0 file*/
  infile regfile lrecl=3339 pad;
  input  @001 Incidence_record $char3339.
         @042 DataItem_20      $char8.
         @145 DataItem_80      $char2.
         @156 DataItem_90      $char3.
         @168 DataItem_130     $char6.
         @428 DataItem_135     $char6.
         @530 DataItem_390     $char8.;
         
  year_of_diag = input(substr(DataItem_390,1,4),4.);
  length census_tract $6.;
  if 1995 <= year_of_diag <= 2004 then do;
     pct_pov_num  = DataItem_80||DataItem_90||DataItem_130;
     pct_pov_cat = input(pct_pov_num,pov9504f.);
     census_tract = DataItem_130;
  end;
  else if 2005 <= year_of_diag <= 2007 then do;
     pct_pov_num  = DataItem_80||DataItem_90||DataItem_130;
     pct_pov_cat = input(pct_pov_num,pov0509f.);
     census_tract = DataItem_130;
  end;
  else if year_of_diag = 2008 then do;
     pct_pov_num  = DataItem_80||DataItem_90||DataItem_135;
     pct_pov_cat = input(pct_pov_num,pov0610f.);
     census_tract = DataItem_135;
  end;
  else if 2009 <= year_of_diag <= 2011 then do;
     pct_pov_num  = DataItem_80||DataItem_90||DataItem_135;
     pct_pov_cat = input(pct_pov_num,pov0711f.); 
     census_tract = DataItem_135;
  end; 
  else do;
     pct_pov_cat = 9;
     census_tract = "";
  end;

  label Incidence_record = 'Incidence Record'
        DataItem_20      = 'Patient ID Number'
        DataItem_80      = 'Addr at DX - State'
        DataItem_90      = 'County at DX'
        DataItem_130     = 'Census tract 2000'
        DataItem_135     = 'Census tract 2010'
        DataItem_390     = 'Date of Diagnosis'
        pct_pov_num      = 'Percent Poverty'
        pct_pov_cat      = 'Percent Poverty - Category'
        year_of_diag     = 'Year of Diagnosis'
        census_tract     = 'Census Tract';
run;

/* Create a new file with state, county, percent poverty variables - Census Tract information (Items #130, #135) is not included*/
data _null_;
  set registry_data;
  file outfile lrecl=3339 pad;
  put @001  Incidence_record $char3339.
      @168  '      '
      @428  '      '
      @463  pct_pov_cat;
  run;

****** Summarize Results;
proc format;
 value pov_cat
       1= '< 5%'
       2= ' 5 -  9.9%'
       3= '10 - 19.9%'
       4= '>= 20%'
       9= 'Unknown';

 value yr_dx
       1995-2004 = "1995-2004"
       2005-2007 = "2005-2007"
            2008 = "2008"
       2009-2011 = "2009-2011"
           Other = "Invalid";
 run;

proc freq data=registry_data;
 tables pct_pov_cat
  /* optional frequncy to verify categories-> pct_pov_cat*pct_pov_num; */
    /list missing;
 title 'NAACCR - Percent Poverty Assignment Program version 4.';
 title2 'Frequency Distributions for Newly Created Percent Poverty Variables';
 format pct_pov_num $11. pct_pov_cat pov_cat.;
 run;

proc freq data=registry_data;
 title2 'Frequency Listing of Census Tracts With No Percent Poverty Information';
 title3 'Because of Incomplete County/Census Tract Information, or Unavailable Poverty Information';
 where pct_pov_num eq "";
 tables DataItem_80*DataItem_90*DataItem_130*DataItem_135*year_of_diag/list missing;
 format year_of_diag yr_dx.;
 run;

proc means data=registry_data noprint nway missing;
 class  DataItem_80 DataItem_90 census_tract pct_pov_cat ;
 where  pct_pov_num ne "";
 output out=freqs n=n;
 run;

proc print data=freqs label noobs;
 var  DataItem_80 DataItem_90 census_tract pct_pov_cat _freq_;
 format pct_pov_cat pov_cat.;
 label _freq_ ='Frequency';
 title2 'Frequency Distributions To Verify Percent Poverty Assignment';
 run;
