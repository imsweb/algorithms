/* this is the file that needs to be translated, just put it in the same folder */
filename fmats 'NHAPIIA_Formats_2013_09_12.xpt';

/* this is just a folder used to create a tmp file and be able to read it again... */
libname lib2 '/home/bekeles/NHAPIIA/printformat/';

proc cimport library=lib2 file=fmats;
proc format fmtlib library=lib2.formats;

/* the output will be a file named 'printformat.lst'; */
/* it also creates two other file (a log and a cab or something); those can be deleted... */


