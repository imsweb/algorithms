/*
 * Copyright (C) 2017 Information Management Services, Inc.
 */
package com.imsweb.algorithms.icd;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;

import org.apache.commons.lang3.math.NumberUtils;

import au.com.bytecode.opencsv.CSVReader;

public class IcdUtils {

    public static final String SEX_MALE = "1";
    public static final String SEX_FEMALE = "2";

    public static final String REPORTABILITY_YES = "Y";
    public static final String REPORTABILITY_OPTIONAL = "O";

    private static final Map<String, IcdConversionEntry> _ICD_9_CM_TO_O3_CONVERSION = new HashMap<>();
    private static final Map<String, IcdConversionEntry> _ICD_10_CM_TO_O3_CONVERSION = new HashMap<>();
    private static final Map<String, IcdConversionEntry> _ICD_10_TO_O3_CONVERSION = new HashMap<>();

    private static final Map<String, String> _ICD_O3_SITE_LOOKUP = new HashMap<>();


    private static final List<String> _ICDO3_TO_ICDO2_LOOKUP_SPECIAL = new ArrayList<>();
    private static final List<String> _ICDO3_TO_ICDO2_LOOKUP = new ArrayList<>();



    static {
        _ICD_O3_SITE_LOOKUP.put("C000", "External upper lip");
        _ICD_O3_SITE_LOOKUP.put("C001", "External lower lip");
        _ICD_O3_SITE_LOOKUP.put("C002", "External lip, NOS");
        _ICD_O3_SITE_LOOKUP.put("C003", "Mucosa of upper lip");
        _ICD_O3_SITE_LOOKUP.put("C004", "Mucosa of lower lip");
        _ICD_O3_SITE_LOOKUP.put("C005", "Mucosa of lip, NOS");
        _ICD_O3_SITE_LOOKUP.put("C006", "Commissure of lip");
        _ICD_O3_SITE_LOOKUP.put("C008", "Overlapping lesion of lip");
        _ICD_O3_SITE_LOOKUP.put("C009", "Lip, NOS");
        _ICD_O3_SITE_LOOKUP.put("C019", "Base of tongue, NOS");
        _ICD_O3_SITE_LOOKUP.put("C020", "Dorsal surface of tongue, NOS");
        _ICD_O3_SITE_LOOKUP.put("C021", "Border of tongue");
        _ICD_O3_SITE_LOOKUP.put("C022", "Ventral surface of tongue, NOS");
        _ICD_O3_SITE_LOOKUP.put("C023", "Anterior 2/3 of tongue, NOS");
        _ICD_O3_SITE_LOOKUP.put("C024", "Lingual tonsil");
        _ICD_O3_SITE_LOOKUP.put("C028", "Overlapping lesion of tongue");
        _ICD_O3_SITE_LOOKUP.put("C029", "Tongue, NOS");
        _ICD_O3_SITE_LOOKUP.put("C030", "Upper gum");
        _ICD_O3_SITE_LOOKUP.put("C031", "Lower gum");
        _ICD_O3_SITE_LOOKUP.put("C039", "Gum, NOS");
        _ICD_O3_SITE_LOOKUP.put("C040", "Anterior floor of mouth");
        _ICD_O3_SITE_LOOKUP.put("C041", "Lateral floor of mouth");
        _ICD_O3_SITE_LOOKUP.put("C048", "Overlapping lesion of floor of mouth");
        _ICD_O3_SITE_LOOKUP.put("C049", "Floor of mouth, NOS");
        _ICD_O3_SITE_LOOKUP.put("C050", "Hard palate");
        _ICD_O3_SITE_LOOKUP.put("C051", "Soft palate, NOS");
        _ICD_O3_SITE_LOOKUP.put("C052", "Uvula");
        _ICD_O3_SITE_LOOKUP.put("C058", "Overlapping lesion of palate");
        _ICD_O3_SITE_LOOKUP.put("C059", "Palate, NOS");
        _ICD_O3_SITE_LOOKUP.put("C060", "Cheek mucosa");
        _ICD_O3_SITE_LOOKUP.put("C061", "Vestibule of mouth");
        _ICD_O3_SITE_LOOKUP.put("C062", "Retromolar area");
        _ICD_O3_SITE_LOOKUP.put("C068", "Overlapping lesion of other and unspecified parts of mouth");
        _ICD_O3_SITE_LOOKUP.put("C069", "Mouth, NOS");
        _ICD_O3_SITE_LOOKUP.put("C079", "Parotid gland");
        _ICD_O3_SITE_LOOKUP.put("C080", "Submandibular gland");
        _ICD_O3_SITE_LOOKUP.put("C081", "Sublingual gland");
        _ICD_O3_SITE_LOOKUP.put("C088", "Overlapping lesion of major salivary glands");
        _ICD_O3_SITE_LOOKUP.put("C089", "Major salivary gland, NOS");
        _ICD_O3_SITE_LOOKUP.put("C090", "Tonsillar fossa");
        _ICD_O3_SITE_LOOKUP.put("C091", "Tonsillar pillar");
        _ICD_O3_SITE_LOOKUP.put("C098", "Overlapping lesion of tonsil");
        _ICD_O3_SITE_LOOKUP.put("C099", "Tonsil, NOS");
        _ICD_O3_SITE_LOOKUP.put("C100", "Vallecula");
        _ICD_O3_SITE_LOOKUP.put("C101", "Anterior surface of epiglottis");
        _ICD_O3_SITE_LOOKUP.put("C102", "Lateral wall of oropharynx");
        _ICD_O3_SITE_LOOKUP.put("C103", "Posterior wall of oropharynx");
        _ICD_O3_SITE_LOOKUP.put("C104", "Branchial cleft");
        _ICD_O3_SITE_LOOKUP.put("C108", "Overlapping lesion of oropharynx");
        _ICD_O3_SITE_LOOKUP.put("C109", "Oropharynx, NOS");
        _ICD_O3_SITE_LOOKUP.put("C110", "Superior wall of nasopharynx");
        _ICD_O3_SITE_LOOKUP.put("C111", "Posterior wall of nasopharynx");
        _ICD_O3_SITE_LOOKUP.put("C112", "Lateral wall of nasopharynx");
        _ICD_O3_SITE_LOOKUP.put("C113", "Anterior wall of nasopharynx");
        _ICD_O3_SITE_LOOKUP.put("C118", "Overlapping lesion of nasopharynx");
        _ICD_O3_SITE_LOOKUP.put("C119", "Nasopharynx, NOS");
        _ICD_O3_SITE_LOOKUP.put("C129", "Pyriform sinus");
        _ICD_O3_SITE_LOOKUP.put("C130", "Postcricoid region");
        _ICD_O3_SITE_LOOKUP.put("C131", "Hypopharyngeal aspect of aryepiglottic fold");
        _ICD_O3_SITE_LOOKUP.put("C132", "Posterior wall of hypopharynx");
        _ICD_O3_SITE_LOOKUP.put("C138", "Overlapping lesion of hypopharynx");
        _ICD_O3_SITE_LOOKUP.put("C139", "Hypopharynx, NOS");
        _ICD_O3_SITE_LOOKUP.put("C140", "Pharynx, NOS");
        _ICD_O3_SITE_LOOKUP.put("C142", "Waldeyer ring");
        _ICD_O3_SITE_LOOKUP.put("C148", "Overlapping lesion of lip, oral cavity and pharynx");
        _ICD_O3_SITE_LOOKUP.put("C150", "Cervical esophagus");
        _ICD_O3_SITE_LOOKUP.put("C151", "Thoracic esophagus");
        _ICD_O3_SITE_LOOKUP.put("C152", "Abdominal esophagus");
        _ICD_O3_SITE_LOOKUP.put("C153", "Upper third of esophagus");
        _ICD_O3_SITE_LOOKUP.put("C154", "Middle third of esophagus");
        _ICD_O3_SITE_LOOKUP.put("C155", "Lower third of esophagus");
        _ICD_O3_SITE_LOOKUP.put("C158", "Overlapping lesion of esophagus");
        _ICD_O3_SITE_LOOKUP.put("C159", "Esophagus, NOS");
        _ICD_O3_SITE_LOOKUP.put("C160", "Cardia, NOS");
        _ICD_O3_SITE_LOOKUP.put("C161", "Fundus of stomach");
        _ICD_O3_SITE_LOOKUP.put("C162", "Body of stomach");
        _ICD_O3_SITE_LOOKUP.put("C163", "Gastric antrum");
        _ICD_O3_SITE_LOOKUP.put("C164", "Pylorus");
        _ICD_O3_SITE_LOOKUP.put("C165", "Lesser curvature of stomach, NOS");
        _ICD_O3_SITE_LOOKUP.put("C166", "Greater curvature of stomach, NOS");
        _ICD_O3_SITE_LOOKUP.put("C168", "Overlapping lesion of stomach");
        _ICD_O3_SITE_LOOKUP.put("C169", "Stomach, NOS");
        _ICD_O3_SITE_LOOKUP.put("C170", "Duodenum");
        _ICD_O3_SITE_LOOKUP.put("C171", "Jejunum");
        _ICD_O3_SITE_LOOKUP.put("C172", "Ileum");
        _ICD_O3_SITE_LOOKUP.put("C173", "Meckel diverticulum");
        _ICD_O3_SITE_LOOKUP.put("C178", "Overlapping lesion of smallintestine");
        _ICD_O3_SITE_LOOKUP.put("C179", "Small intestine, NOS");
        _ICD_O3_SITE_LOOKUP.put("C180", "Cecum");
        _ICD_O3_SITE_LOOKUP.put("C181", "Appendix");
        _ICD_O3_SITE_LOOKUP.put("C182", "Ascending colon");
        _ICD_O3_SITE_LOOKUP.put("C183", "Hepatic flexure of colon");
        _ICD_O3_SITE_LOOKUP.put("C184", "Transverse colon");
        _ICD_O3_SITE_LOOKUP.put("C185", "Splenic flexure of colon");
        _ICD_O3_SITE_LOOKUP.put("C186", "Descending colon");
        _ICD_O3_SITE_LOOKUP.put("C187", "Sigmoid colon");
        _ICD_O3_SITE_LOOKUP.put("C188", "Overlapping lesion of colon");
        _ICD_O3_SITE_LOOKUP.put("C189", "Colon, NOS");
        _ICD_O3_SITE_LOOKUP.put("C199", "Rectosigmoid junction");
        _ICD_O3_SITE_LOOKUP.put("C209", "Rectum, NOS");
        _ICD_O3_SITE_LOOKUP.put("C210", "Anus, NOS");
        _ICD_O3_SITE_LOOKUP.put("C211", "Anal canal");
        _ICD_O3_SITE_LOOKUP.put("C212", "Cloacogenic zone");
        _ICD_O3_SITE_LOOKUP.put("C218", "Overlapping lesion of rectum, anus and anal canal");
        _ICD_O3_SITE_LOOKUP.put("C220", "Liver");
        _ICD_O3_SITE_LOOKUP.put("C221", "Intrahepatic bile duct");
        _ICD_O3_SITE_LOOKUP.put("C239", "Gallbladder");
        _ICD_O3_SITE_LOOKUP.put("C240", "Extrahepatic bile duct");
        _ICD_O3_SITE_LOOKUP.put("C241", "Ampulla of Vater");
        _ICD_O3_SITE_LOOKUP.put("C248", "Overlapping lesion of biliary tract Note:");
        _ICD_O3_SITE_LOOKUP.put("C249", "Biliary tract, NOS");
        _ICD_O3_SITE_LOOKUP.put("C250", "Head of pancreas");
        _ICD_O3_SITE_LOOKUP.put("C251", "Body of pancreas");
        _ICD_O3_SITE_LOOKUP.put("C252", "Tail of pancreas");
        _ICD_O3_SITE_LOOKUP.put("C253", "Pancreatic duct");
        _ICD_O3_SITE_LOOKUP.put("C254", "Islets of Langerhans");
        _ICD_O3_SITE_LOOKUP.put("C257", "Other specified parts of pancreas");
        _ICD_O3_SITE_LOOKUP.put("C258", "Overlapping lesion of pancreas");
        _ICD_O3_SITE_LOOKUP.put("C259", "Pancreas, NOS");
        _ICD_O3_SITE_LOOKUP.put("C260", "Intestinal tract, NOS");
        _ICD_O3_SITE_LOOKUP.put("C268", "Overlapping lesion of digestive system");
        _ICD_O3_SITE_LOOKUP.put("C269", "Gastrointestinal tract, NOS");
        _ICD_O3_SITE_LOOKUP.put("C300", "Nasal cavity");
        _ICD_O3_SITE_LOOKUP.put("C301", "Middle ear");
        _ICD_O3_SITE_LOOKUP.put("C310", "Maxillary sinus");
        _ICD_O3_SITE_LOOKUP.put("C311", "Ethmoid sinus");
        _ICD_O3_SITE_LOOKUP.put("C312", "Frontal sinus");
        _ICD_O3_SITE_LOOKUP.put("C313", "Sphenoid sinus");
        _ICD_O3_SITE_LOOKUP.put("C318", "Overlapping lesion of accessory sinuses");
        _ICD_O3_SITE_LOOKUP.put("C319", "Accessory sinus, NOS");
        _ICD_O3_SITE_LOOKUP.put("C320", "Glottis");
        _ICD_O3_SITE_LOOKUP.put("C321", "Supraglottis");
        _ICD_O3_SITE_LOOKUP.put("C322", "Subglottis");
        _ICD_O3_SITE_LOOKUP.put("C323", "Laryngeal cartilage");
        _ICD_O3_SITE_LOOKUP.put("C328", "Overlapping lesion of larynx");
        _ICD_O3_SITE_LOOKUP.put("C329", "Larynx, NOS");
        _ICD_O3_SITE_LOOKUP.put("C339", "Trachea");
        _ICD_O3_SITE_LOOKUP.put("C340", "Main bronchus");
        _ICD_O3_SITE_LOOKUP.put("C341", "Upper lobe, lung");
        _ICD_O3_SITE_LOOKUP.put("C342", "Middle lobe, lung");
        _ICD_O3_SITE_LOOKUP.put("C343", "Lower lobe, lung");
        _ICD_O3_SITE_LOOKUP.put("C348", "Overlapping lesion of lung");
        _ICD_O3_SITE_LOOKUP.put("C349", "Lung, NOS");
        _ICD_O3_SITE_LOOKUP.put("C379", "Thymus");
        _ICD_O3_SITE_LOOKUP.put("C380", "Heart");
        _ICD_O3_SITE_LOOKUP.put("C381", "Anterior mediastinum");
        _ICD_O3_SITE_LOOKUP.put("C382", "Posterior mediastinum");
        _ICD_O3_SITE_LOOKUP.put("C383", "Mediastinum, NOS");
        _ICD_O3_SITE_LOOKUP.put("C384", "Pleura, NOS");
        _ICD_O3_SITE_LOOKUP.put("C388", "Overlapping lesion of heart, mediastinum and pleura");
        _ICD_O3_SITE_LOOKUP.put("C390", "Upper respiratory tract, NOS");
        _ICD_O3_SITE_LOOKUP.put("C398", "Overlapping lesion of respiratory system and intrathoracic organs");
        _ICD_O3_SITE_LOOKUP.put("C399", "Ill-defined sites within respiratory system");
        _ICD_O3_SITE_LOOKUP.put("C400", "Long bones of upper limb, scapula and associated joints");
        _ICD_O3_SITE_LOOKUP.put("C401", "Short bones of upper limb and associated joints");
        _ICD_O3_SITE_LOOKUP.put("C402", "Long bones of lower limb and associated joints");
        _ICD_O3_SITE_LOOKUP.put("C403", "Short bones of lower limb and associated joints");
        _ICD_O3_SITE_LOOKUP.put("C408", "Overlapping lesion of bones, joints and articular cartilage of limbs");
        _ICD_O3_SITE_LOOKUP.put("C409", "Bone of limb, NOS");
        _ICD_O3_SITE_LOOKUP.put("C410", "Bones of skull and face and associated joints");
        _ICD_O3_SITE_LOOKUP.put("C411", "Mandible");
        _ICD_O3_SITE_LOOKUP.put("C412", "Vertebral column");
        _ICD_O3_SITE_LOOKUP.put("C413", "Rib, sternum, clavicle and associated joints");
        _ICD_O3_SITE_LOOKUP.put("C414", "Pelvic bones, sacrum, coccyx and associated joints");
        _ICD_O3_SITE_LOOKUP.put("C418", "Overlapping lesion of bones, joints and articular cartilage");
        _ICD_O3_SITE_LOOKUP.put("C419", "Bone, NOS");
        _ICD_O3_SITE_LOOKUP.put("C420", "Blood");
        _ICD_O3_SITE_LOOKUP.put("C421", "Bone marrow");
        _ICD_O3_SITE_LOOKUP.put("C422", "Spleen");
        _ICD_O3_SITE_LOOKUP.put("C423", "Reticuloendothelial system,NOS");
        _ICD_O3_SITE_LOOKUP.put("C424", "Hematopoietic system, NOS");
        _ICD_O3_SITE_LOOKUP.put("C440", "Skin of lip, NOS");
        _ICD_O3_SITE_LOOKUP.put("C441", "Eyelid");
        _ICD_O3_SITE_LOOKUP.put("C442", "External ear");
        _ICD_O3_SITE_LOOKUP.put("C443", "Skin of other and unspecified parts of face");
        _ICD_O3_SITE_LOOKUP.put("C444", "Skin of scalp and neck");
        _ICD_O3_SITE_LOOKUP.put("C445", "Skin of trunk");
        _ICD_O3_SITE_LOOKUP.put("C446", "Skin of upper limb and shoulder");
        _ICD_O3_SITE_LOOKUP.put("C447", "Skin of lower limb and hip");
        _ICD_O3_SITE_LOOKUP.put("C448", "Overlapping lesion of skin");
        _ICD_O3_SITE_LOOKUP.put("C449", "Skin, NOS");
        _ICD_O3_SITE_LOOKUP.put("C470", "Peripheral nerves and autonomic nervous system of head, face, and neck");
        _ICD_O3_SITE_LOOKUP.put("C471", "Peripheral nerves and autonomic nervous system of upper limb and shoulder");
        _ICD_O3_SITE_LOOKUP.put("C472", "Peripheral nerves and autonomic nervous system of lower limband hip");
        _ICD_O3_SITE_LOOKUP.put("C473", "Peripheral nerves and autonomic nervous system of thorax");
        _ICD_O3_SITE_LOOKUP.put("C474", "Peripheral nerves and autonomic nervous system of abdomen");
        _ICD_O3_SITE_LOOKUP.put("C475", "Peripheral nerves and autonomic nervous system of pelvis");
        _ICD_O3_SITE_LOOKUP.put("C476", "Peripheral nerves and autonomic nervous system of trunk, NOS");
        _ICD_O3_SITE_LOOKUP.put("C478", "Overlapping lesion of peripheral nerves and autonomic nervous system");
        _ICD_O3_SITE_LOOKUP.put("C479", "Autonomic nervous system, NOS");
        _ICD_O3_SITE_LOOKUP.put("C480", "Retroperitoneum");
        _ICD_O3_SITE_LOOKUP.put("C481", "Specified parts of peritoneum");
        _ICD_O3_SITE_LOOKUP.put("C482", "Peritoneum, NOS");
        _ICD_O3_SITE_LOOKUP.put("C488", "Overlapping lesion of retroperitoneum and peritoneum");
        _ICD_O3_SITE_LOOKUP.put("C490", "Connective, subcutaneous and other soft tissues of head, face, and neck");
        _ICD_O3_SITE_LOOKUP.put("C491", "Connective, subcutaneous and other soft tissues of upper limb and shoulder");
        _ICD_O3_SITE_LOOKUP.put("C492", "Connective, subcutaneous and other soft tissues of lower limb and hip");
        _ICD_O3_SITE_LOOKUP.put("C493", "Connective, subcutaneous and other soft tissues of thorax");
        _ICD_O3_SITE_LOOKUP.put("C494", "Connective, subcutaneous and other soft tissues of abdomen");
        _ICD_O3_SITE_LOOKUP.put("C495", "Connective, subcutaneous and other soft tissues of pelvis");
        _ICD_O3_SITE_LOOKUP.put("C496", "Connective, subcutaneous and other soft tissues of trunk NOS");
        _ICD_O3_SITE_LOOKUP.put("C498", "Overlapping lesion of connective, subcutaneous and other soft tissues");
        _ICD_O3_SITE_LOOKUP.put("C499", "Connective, subcutaneous and other soft tissues, NOS");
        _ICD_O3_SITE_LOOKUP.put("C500", "Nipple");
        _ICD_O3_SITE_LOOKUP.put("C501", "Central portion of breast");
        _ICD_O3_SITE_LOOKUP.put("C502", "Upper-inner quadrant of breast");
        _ICD_O3_SITE_LOOKUP.put("C503", "Lower-inner quadrant of breast");
        _ICD_O3_SITE_LOOKUP.put("C504", "Upper-outer quadrant of breast");
        _ICD_O3_SITE_LOOKUP.put("C505", "Lower-outer quadrant of breast");
        _ICD_O3_SITE_LOOKUP.put("C506", "Axillary tail of breast");
        _ICD_O3_SITE_LOOKUP.put("C508", "Overlapping lesion of breast");
        _ICD_O3_SITE_LOOKUP.put("C509", "Breast, NOS");
        _ICD_O3_SITE_LOOKUP.put("C510", "Labium majus");
        _ICD_O3_SITE_LOOKUP.put("C511", "Labium minus");
        _ICD_O3_SITE_LOOKUP.put("C512", "Clitoris");
        _ICD_O3_SITE_LOOKUP.put("C518", "Overlapping lesion of vulva");
        _ICD_O3_SITE_LOOKUP.put("C519", "Vulva, NOS");
        _ICD_O3_SITE_LOOKUP.put("C529", "Vagina, NOS");
        _ICD_O3_SITE_LOOKUP.put("C530", "Endocervix");
        _ICD_O3_SITE_LOOKUP.put("C531", "Exocervix");
        _ICD_O3_SITE_LOOKUP.put("C538", "Overlapping lesion of cervix uteri");
        _ICD_O3_SITE_LOOKUP.put("C539", "Cervix uteri");
        _ICD_O3_SITE_LOOKUP.put("C540", "Isthmus uteri");
        _ICD_O3_SITE_LOOKUP.put("C541", "Endometrium");
        _ICD_O3_SITE_LOOKUP.put("C542", "Myometrium");
        _ICD_O3_SITE_LOOKUP.put("C543", "Fundus uteri");
        _ICD_O3_SITE_LOOKUP.put("C548", "Overlapping lesion of corpus uteri");
        _ICD_O3_SITE_LOOKUP.put("C549", "Corpus uteri");
        _ICD_O3_SITE_LOOKUP.put("C559", "Uterus, NOS");
        _ICD_O3_SITE_LOOKUP.put("C569", "Ovary");
        _ICD_O3_SITE_LOOKUP.put("C570", "Fallopian tube");
        _ICD_O3_SITE_LOOKUP.put("C571", "Broad ligament");
        _ICD_O3_SITE_LOOKUP.put("C572", "Round ligament");
        _ICD_O3_SITE_LOOKUP.put("C573", "Parametrium");
        _ICD_O3_SITE_LOOKUP.put("C574", "Uterine adnexa");
        _ICD_O3_SITE_LOOKUP.put("C577", "Other specified parts of female genital organs");
        _ICD_O3_SITE_LOOKUP.put("C578", "Overlapping lesion of female genital organs");
        _ICD_O3_SITE_LOOKUP.put("C579", "Female genital tract, NOS");
        _ICD_O3_SITE_LOOKUP.put("C589", "Placenta");
        _ICD_O3_SITE_LOOKUP.put("C600", "Prepuce");
        _ICD_O3_SITE_LOOKUP.put("C601", "Glans penis");
        _ICD_O3_SITE_LOOKUP.put("C602", "Body of penis");
        _ICD_O3_SITE_LOOKUP.put("C608", "Overlapping lesion of penis");
        _ICD_O3_SITE_LOOKUP.put("C609", "Penis, NOS");
        _ICD_O3_SITE_LOOKUP.put("C619", "Prostate gland");
        _ICD_O3_SITE_LOOKUP.put("C620", "Undescended testis");
        _ICD_O3_SITE_LOOKUP.put("C621", "Descended testis");
        _ICD_O3_SITE_LOOKUP.put("C629", "Testis, NOS");
        _ICD_O3_SITE_LOOKUP.put("C630", "Epididymis");
        _ICD_O3_SITE_LOOKUP.put("C631", "Spermatic cord");
        _ICD_O3_SITE_LOOKUP.put("C632", "Scrotum, NOS");
        _ICD_O3_SITE_LOOKUP.put("C637", "Other specified parts of male genital organs");
        _ICD_O3_SITE_LOOKUP.put("C638", "Overlapping lesion of male genital organs Note:");
        _ICD_O3_SITE_LOOKUP.put("C639", "Male genital organs, NOS");
        _ICD_O3_SITE_LOOKUP.put("C649", "Kidney, NOS");
        _ICD_O3_SITE_LOOKUP.put("C659", "Renal pelvis");
        _ICD_O3_SITE_LOOKUP.put("C669", "Ureter");
        _ICD_O3_SITE_LOOKUP.put("C670", "Trigone of bladder");
        _ICD_O3_SITE_LOOKUP.put("C671", "Dome of bladder");
        _ICD_O3_SITE_LOOKUP.put("C672", "Lateral wall of bladder");
        _ICD_O3_SITE_LOOKUP.put("C673", "Anterior wall of bladder");
        _ICD_O3_SITE_LOOKUP.put("C674", "Posterior wall of bladder");
        _ICD_O3_SITE_LOOKUP.put("C675", "Bladder neck");
        _ICD_O3_SITE_LOOKUP.put("C676", "Ureteric orifice");
        _ICD_O3_SITE_LOOKUP.put("C677", "Urachus");
        _ICD_O3_SITE_LOOKUP.put("C678", "Overlapping lesion of bladder");
        _ICD_O3_SITE_LOOKUP.put("C679", "Bladder, NOS");
        _ICD_O3_SITE_LOOKUP.put("C680", "Urethra");
        _ICD_O3_SITE_LOOKUP.put("C681", "Paraurethral gland");
        _ICD_O3_SITE_LOOKUP.put("C688", "Overlapping lesion of urinary organs");
        _ICD_O3_SITE_LOOKUP.put("C689", "Urinary system, NOS");
        _ICD_O3_SITE_LOOKUP.put("C690", "Conjunctiva");
        _ICD_O3_SITE_LOOKUP.put("C691", "Cornea, NOS");
        _ICD_O3_SITE_LOOKUP.put("C692", "Retina");
        _ICD_O3_SITE_LOOKUP.put("C693", "Choroid");
        _ICD_O3_SITE_LOOKUP.put("C694", "Ciliary body");
        _ICD_O3_SITE_LOOKUP.put("C695", "Lacrimal gland");
        _ICD_O3_SITE_LOOKUP.put("C696", "Orbit, NOS");
        _ICD_O3_SITE_LOOKUP.put("C698", "Overlapping lesion of eye and adnexa");
        _ICD_O3_SITE_LOOKUP.put("C699", "Eye, NOS");
        _ICD_O3_SITE_LOOKUP.put("C700", "Cerebral meninges");
        _ICD_O3_SITE_LOOKUP.put("C701", "Spinal meninges");
        _ICD_O3_SITE_LOOKUP.put("C709", "Meninges, NOS");
        _ICD_O3_SITE_LOOKUP.put("C710", "Cerebrum");
        _ICD_O3_SITE_LOOKUP.put("C711", "Frontal lobe");
        _ICD_O3_SITE_LOOKUP.put("C712", "Temporal lobe");
        _ICD_O3_SITE_LOOKUP.put("C713", "Parietal lobe");
        _ICD_O3_SITE_LOOKUP.put("C714", "Occipital lobe");
        _ICD_O3_SITE_LOOKUP.put("C715", "Ventricle, NOS");
        _ICD_O3_SITE_LOOKUP.put("C716", "Cerebellum, NOS");
        _ICD_O3_SITE_LOOKUP.put("C717", "Brain stem");
        _ICD_O3_SITE_LOOKUP.put("C718", "Overlapping lesion of brain");
        _ICD_O3_SITE_LOOKUP.put("C719", "Brain, NOS");
        _ICD_O3_SITE_LOOKUP.put("C720", "Spinal cord");
        _ICD_O3_SITE_LOOKUP.put("C721", "Cauda equina");
        _ICD_O3_SITE_LOOKUP.put("C722", "Olfactory nerve");
        _ICD_O3_SITE_LOOKUP.put("C723", "Optic nerve");
        _ICD_O3_SITE_LOOKUP.put("C724", "Acoustic nerve");
        _ICD_O3_SITE_LOOKUP.put("C725", "Cranial nerve, NOS");
        _ICD_O3_SITE_LOOKUP.put("C728", "Overlapping lesion of brain and central nervous system");
        _ICD_O3_SITE_LOOKUP.put("C729", "Nervous system, NOS");
        _ICD_O3_SITE_LOOKUP.put("C739", "Thyroid gland");
        _ICD_O3_SITE_LOOKUP.put("C740", "Cortex of adrenal gland");
        _ICD_O3_SITE_LOOKUP.put("C741", "Medulla of adrenal gland");
        _ICD_O3_SITE_LOOKUP.put("C749", "Adrenal gland, NOS");
        _ICD_O3_SITE_LOOKUP.put("C750", "Parathyroid gland");
        _ICD_O3_SITE_LOOKUP.put("C751", "Pituitary gland");
        _ICD_O3_SITE_LOOKUP.put("C752", "Craniopharyngeal duct");
        _ICD_O3_SITE_LOOKUP.put("C753", "Pineal gland");
        _ICD_O3_SITE_LOOKUP.put("C754", "Carotid body");
        _ICD_O3_SITE_LOOKUP.put("C755", "Aortic body and other paraganglia");
        _ICD_O3_SITE_LOOKUP.put("C758", "Overlapping lesion of endocrine glands and related structures");
        _ICD_O3_SITE_LOOKUP.put("C759", "Endocrine gland, NOS");
        _ICD_O3_SITE_LOOKUP.put("C760", "Head, face or neck, NOS");
        _ICD_O3_SITE_LOOKUP.put("C761", "Thorax, NOS");
        _ICD_O3_SITE_LOOKUP.put("C762", "Abdomen, NOS");
        _ICD_O3_SITE_LOOKUP.put("C763", "Pelvis, NOS");
        _ICD_O3_SITE_LOOKUP.put("C764", "Upper limb, NOS");
        _ICD_O3_SITE_LOOKUP.put("C765", "Lower limb, NOS");
        _ICD_O3_SITE_LOOKUP.put("C767", "Other ill-defined sites");
        _ICD_O3_SITE_LOOKUP.put("C768", "Overlapping lesion of ill-defined sites");
        _ICD_O3_SITE_LOOKUP.put("C770", "Lymph nodes of head, face and neck");
        _ICD_O3_SITE_LOOKUP.put("C771", "Intrathoracic lymph nodes");
        _ICD_O3_SITE_LOOKUP.put("C772", "Intra-abdominal lymph nodes");
        _ICD_O3_SITE_LOOKUP.put("C773", "Lymph nodes of axilla or arm");
        _ICD_O3_SITE_LOOKUP.put("C774", "Lymph nodes of inguinal region or leg");
        _ICD_O3_SITE_LOOKUP.put("C775", "Pelvic lymph nodes");
        _ICD_O3_SITE_LOOKUP.put("C778", "Lymph nodes of multiple regions");
        _ICD_O3_SITE_LOOKUP.put("C779", "Lymph node, NOS");
        _ICD_O3_SITE_LOOKUP.put("C809", "Unknown primary site");
    }

    /**
     * Initializes the ICD data (this method is called lazily if needed).
     */
    public static synchronized void initalize() {

        loadIcdo3to2DataFile("morph.special.txt", _ICDO3_TO_ICDO2_LOOKUP_SPECIAL);
        loadIcdo3to2DataFile("morph.txt", _ICDO3_TO_ICDO2_LOOKUP);


        if (!_ICD_9_CM_TO_O3_CONVERSION.isEmpty())
            return;

        loadDataFile("icd-9-cm-to-icd-o-3.csv", _ICD_9_CM_TO_O3_CONVERSION);
        loadDataFile("icd-10-cm-to-icd-o-3.csv", _ICD_10_CM_TO_O3_CONVERSION);
        loadDataFile("icd-10-to-icd-o-3.csv", _ICD_10_TO_O3_CONVERSION);

    }

    private static void loadDataFile(String file, Map<String, IcdConversionEntry> result) {
        try (CSVReader reader = new CSVReader(new InputStreamReader(Thread.currentThread().getContextClassLoader().getResourceAsStream("icd/" + file), StandardCharsets.US_ASCII), ',', '"', '\\', 1)) {
            for (String[] row : reader.readAll()) {
                if (row.length != 8)
                    throw new RuntimeException("Was expecting 8 values, got " + row.length + " - " + Arrays.toString(row));

                IcdConversionEntry entry = new IcdConversionEntry();
                entry.setSourceCode(row[0]);
                entry.setTargetCode(row[1]);
                entry.setHistology(row[2].isEmpty() ? null : row[2]);
                entry.setBehavior(row[3].isEmpty() ? null : row[3]);
                entry.setGrade(row[4].isEmpty() ? null : row[4]);
                entry.setLaterality(row[5].isEmpty() ? null : row[5]);
                entry.setReportable(row[6].isEmpty() ? null : row[6]);
                entry.setSex(row[7].isEmpty() ? null : row[7]);

                // the key will contain the sex if it's not blank in the data files (blank means the value applies for any sex value)
                if (entry.getSex() != null)
                    result.put(entry.getSourceCode() + entry.getSex(), entry);
                else
                    result.put(entry.getSourceCode(), entry);
            }
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Returns the internal conversions from ICD-9CM to ICD-O-3.
     * @return internal conversions as a map
     */
    public static Map<String, IcdConversionEntry> getIcd9CmToO3Conversions() {
        initalize();

        return Collections.unmodifiableMap(_ICD_9_CM_TO_O3_CONVERSION);
    }

    /**
     * Returns the internal conversions from ICD-10CM to ICD-O-3.
     * @return internal conversions as a map
     */
    public static Map<String, IcdConversionEntry> getIcd10CmToO3Conversions() {
        initalize();

        return Collections.unmodifiableMap(_ICD_10_CM_TO_O3_CONVERSION);
    }

    /**
     * Returns the internal conversions from ICD-10 to ICD-O-3.
     * @return internal conversions as a map
     */
    public static Map<String, IcdConversionEntry> getIcd10ToO3Conversions() {
        initalize();

        return Collections.unmodifiableMap(_ICD_10_TO_O3_CONVERSION);
    }

    /**
     * Return the ICD-O-3 site lookup (codes and labels).
     */
    public static Map<String, String> getIcdo3SiteLookup() {
        return Collections.unmodifiableMap(_ICD_O3_SITE_LOOKUP);
    }

    /**
     * Returns the ICD-O-3 conversion entry for the provided ICD9-CM code.
     * @param icd9CmCode ICD9-CM code (required)
     * @param sex sex value, optional. If provided, needs to be "1" (MALE) or "2" (FEMALE), otherwise it will be ignored.
     * @return corresponding ICD-O-3 conversion entry, maybe null
     */
    public static IcdConversionEntry getIcdo3FromIcd9Cm(String icd9CmCode, String sex) {
        initalize();

        IcdConversionEntry result = null;

        // if we have a value for the sex, try to use it
        if (sex != null && (SEX_MALE.equals(sex) || SEX_FEMALE.equals(sex)))
            result = _ICD_9_CM_TO_O3_CONVERSION.get(icd9CmCode + sex);

        // if we didn't find any result, try without the sex
        if (result == null)
            result = _ICD_9_CM_TO_O3_CONVERSION.get(icd9CmCode);

        // if we still didn't get a value and no sex was provided, assume MALE
        if (result == null && sex == null)
            result = _ICD_9_CM_TO_O3_CONVERSION.get(icd9CmCode + SEX_MALE);

        return result;

    }

    /**
     * Returns the ICD-O-3 conversion entry for the provided ICD10-CM code.
     * @param icd10CmCode ICD10-CM code (required)
     * @return corresponding ICD-O-3 conversion entry, maybe null
     */
    public static IcdConversionEntry getIcdo3FromIcd10Cm(String icd10CmCode) {
        initalize();

        return _ICD_10_CM_TO_O3_CONVERSION.get(icd10CmCode);
    }

    /**
     * Returns the ICD-O-3 conversion entry for the provided ICD10 code.
     * @param icd10Code ICD10 code (required)
     * @return corresponding ICD-O-3 conversion entry, maybe null
     */
    public static IcdConversionEntry getIcdo3FromIcd10(String icd10Code) {
        return getIcdo3FromIcd10(icd10Code, true);
    }

    /**
     * Returns the ICD-O-3 conversion entry for the provided ICD10 code.
     * @param icd10Code ICD10 code (required)
     * @param allowNullResult if set to false, then a fake result will be returned if the internal data doesn't contain the requested code.
     * @return corresponding ICD-O-3 conversion entry, maybe null (depending on allowNullResult)
     */
    public static IcdConversionEntry getIcdo3FromIcd10(String icd10Code, boolean allowNullResult) {
        initalize();

        String code = icd10Code == null ? null : icd10Code.toUpperCase();
        if (code != null && code.length() == 3)
            code = code + "9";

        IcdConversionEntry result = _ICD_10_TO_O3_CONVERSION.get(code);

        // if the data doesn't contain the code, build a fake one if we have to
        if (result == null && !allowNullResult) {
            result = new IcdConversionEntry();
            result.setSourceCode(code);
            result.setTargetCode(_ICD_O3_SITE_LOOKUP.containsKey(code) ? code : "C809");
            result.setHistology("8000");
            result.setBehavior(icd10Code != null && icd10Code.startsWith("C") ? "3" : icd10Code != null && icd10Code.startsWith("D") ? "2" : null);
            result.setGrade("9");
            result.setLaterality(null);
            result.setReportable(null);
            result.setSex(null);
        }
        return result;
    }








    private static final int MORPH_SPECIAL_LINES   = 82;

    private static final int FLAG_NONE             = 0;
    private static final int FLAG_INVALID          = 1;
    private static final int FLAG_HAND_REVIEW      = 2;
    private static final int FLAG_INVALID_SITE     = 3;
    private static final int FLAG_INVALID_HIST     = 4;
    private static final int FLAG_INVALID_BEH      = 5;


    /**
     * Loads a lookup data file for use in getIcdo2FromIcdo3().
     * @param file The name of the test file to load.
     * @param result The list of strings which will contain the strings from the file.
     */
    private static void loadIcdo3to2DataFile(String file, List<String> result) {

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(Thread.currentThread().getContextClassLoader().getResourceAsStream("icd/" + file), StandardCharsets.US_ASCII))) {
            String line;
            while ((line = reader.readLine()) != null) {
                // process the line.
                result.add(line.trim());
            }
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    /**
     * Returns the ICD-O-2 conversion entry for the provided ICD-O-3 code.
     * @param icdo3Code ICD-O-3 code (site)(required)
     * @param icdo3Hist ICD-O-3 histology (required)
     * @param icdo3Beh ICD-O-3 behavior (required)
     * @param allowNullResult if set to false, then a fake result will be returned if the internal data doesn't contain the requested code.
     * @return corresponding ICD-O-2 conversion entry, maybe null (depending on allowNullResult)
     */
    public static IcdConversionEntry getIcdo2FromIcdo3(String icdo3Code, String icdo3Hist, String icdo3Beh, boolean allowNullResult) {
        initalize();

        IcdConversionEntry result = null;

        int iFlags = CheckInvalid(icdo3Code, icdo3Hist, icdo3Beh);
        if (iFlags == FLAG_NONE)
        {
            /////////////////////////////////////////////////////////////////////////
            // Conversion function calls:
            //   ConvertMorph:      Search by Hist.  Provides base ICD-O-2 Histology.
            //                      Assumes behavior is unchanged.  If hand review flag
            //                      is not 'X', set Hand Review='1'.  If Beh flag is not
            //                      'X', calls ConvertMorphSpec.  If site flag is not
            //                      'X', calls ConvertSite.
            //   ConvertMorphSpec:  Search by Hist/Beh. Called if Beh flag is not 'X'.
            //   ConvertSite:       Site specific conversions. Hard coded.  Called if
            //                      Site flag is not 'X'.
            //
            /////////////////////////////////////////////////////////////////////////

            int iHist = Integer.parseInt(icdo3Hist);
            result = new IcdConversionEntry();
            result.setSourceCode(icdo3Code);
            result.setTargetCode(icdo3Code);

            iFlags = ConvertMorph(icdo3Code, iHist, icdo3Beh, result);
            if ((iFlags != FLAG_NONE) && (iFlags != FLAG_HAND_REVIEW))
            {
                result = null;
            }
        }

        // if the data doesn't contain the code, build a fake one if we have to
        if (result == null && !allowNullResult) {
            result = new IcdConversionEntry();
            result.setSourceCode(icdo3Code);
            result.setTargetCode(icdo3Code);
            result.setHistology("9999");
            result.setBehavior("9");
            result.setGrade(null);
            result.setLaterality(null);
            result.setReportable(null);
            result.setSex(null);
        }
        return result;
    }

    /**
     * Checks the incoming ICD-O-3 codes for values which are out of range. Sets appropriate flags and returns 1 if any of the flags have been turned on.
     * @param sSite ICD-O-3 code (site)
     * @param sHist ICD-O-3 histology
     * @param sBeh ICD-O-3 behavior
     * @return new iFlags value.
     */
    private static int CheckInvalid(String sSite, String sHist, String sBeh)
    {
        int iFlagsSet = FLAG_NONE;

        // Check for invalid data being entered (Out of range, not specific codes)
        // Site out of range
        if (sSite == null)
            iFlagsSet = FLAG_INVALID_SITE;
        else if (sSite.length() != 4)
            iFlagsSet = FLAG_INVALID_SITE;
        else if ((!sSite.startsWith("C") && !sSite.startsWith("c")) || (!NumberUtils.isDigits(sSite.substring(1, 4).trim())))
            iFlagsSet = FLAG_INVALID_SITE;
        else
        {
           int iSite = Integer.parseInt(sSite.substring(1, 4).trim());
           if (iSite < 000 || iSite > 809)
               iFlagsSet = FLAG_INVALID_SITE;
        }

        // Hist out of range
        if (!NumberUtils.isDigits(sHist.trim()))
            iFlagsSet = FLAG_INVALID_HIST;
        else
        {
            int iHist = Integer.parseInt(sHist.trim());
            if (iHist < 8000 || iHist > 9999)
                iFlagsSet = FLAG_INVALID_HIST;
        }

        // Behavior invalid
        if ((!sBeh.equals("0")) && (!sBeh.equals("1")) && (!sBeh.equals("2")) && (!sBeh.equals("3")) && (!sBeh.equals("6")) && (!sBeh.equals("9")))
            iFlagsSet = FLAG_INVALID_BEH;

        return iFlagsSet;
    }

    /**
     * Takes ICD-O-3 Hist code and retrieves the most common ICD-O-2 Hist code.  Assumes that Beh remains the same.  Also retrieves the hand review flag,
     * the behavior specific conversion flag (which may cause ConvertMorphSpec to be called) and the site specific conversion flag (which may cause ConvertSite to be called).
     *    FLAG values:
     *    0-3, 6, 9 -> this behavior is sent to next function
     *    A         -> all behaviors have flag set
     *    X         -> Invalid morph, not in ICD-O-3     * @param sSite ICD-O-3 code (site)
     * @param iHist ICD-O-3 histology number
     * @param sBeh ICD-O-3 behavior
     * @param result corresponding ICD-O-2 conversion entry.
     * @return new iFlags value.
     */
    private static int ConvertMorph(String sSite, int iHist, String sBeh, IcdConversionEntry result)
    {
        int iFlagsSet = FLAG_NONE;
        final int HAND_FLAG_POS = 4;
        final int BEH_FLAG_POS = 5;
        final int SITE_FLAG_POS = 6;

        String sMorphEntry = _ICDO3_TO_ICDO2_LOOKUP.get(iHist-8000);

        //is this an invalid Histology?
        if (sMorphEntry.equals("9999XXX"))
            iFlagsSet = FLAG_INVALID_HIST;
        else
        {
            //Get standard ICD-O-2 hist and beh
            result.setHistology(sMorphEntry.substring(0, 4));
            result.setBehavior(sBeh);

            //Set hand review flag if needed
            String sHandFlag = sMorphEntry.substring(HAND_FLAG_POS, HAND_FLAG_POS + 1);
            if (sHandFlag.equals("A") || sHandFlag.equals(sBeh))
                iFlagsSet = FLAG_HAND_REVIEW;
            else if (iHist == 8402 && sBeh.equals("3"))
                iFlagsSet = FLAG_HAND_REVIEW;

            //Is there a special behavior based conversion?
            String sBehFlag = sMorphEntry.substring(BEH_FLAG_POS, BEH_FLAG_POS + 1);
            if (sBehFlag.equals(sBeh) || sBehFlag.equals("A"))
                iFlagsSet = ConvertMorphSpec(iHist, sBeh, result);

            //Is there a special site based conversion?
            String sSiteFlag = sMorphEntry.substring(SITE_FLAG_POS, SITE_FLAG_POS + 1);
            if (sSiteFlag.equals(sBeh))
                iFlagsSet = ConvertSite(sSite, iHist, sBeh, result);
        }

        return iFlagsSet;
    }

    /**
     * Takes an ICD-O-3 Hist & Behavior and retrieves the ICD-O-2 Hist and Behavior.
     * @param iHist ICD-O-3 histology number
     * @param sBeh ICD-O-3 behavior
     * @param result corresponding ICD-O-2 conversion entry.
     * @return new iFlags value.
     */
    private static int ConvertMorphSpec(int iHist, String sBeh, IcdConversionEntry result)
    {
        final int HIST_FLAG_POS = 0;
        final int BEH3_FLAG_POS = 4;
        final int HIST2_FLAG_POS = 5;
        final int BEH2_FLAG_POS = 9;

        String  sSpecEntry, sHist;
        boolean bIsFound = false;
        String sHistFlag;
        String sBeh3Flag;

        sHist = Integer.toString(iHist);

        for (int i=0; i < MORPH_SPECIAL_LINES && !bIsFound; i++)
        {
            sSpecEntry = _ICDO3_TO_ICDO2_LOOKUP_SPECIAL.get(i);

            sHistFlag = sSpecEntry.substring(HIST_FLAG_POS, HIST_FLAG_POS + 4);
            sBeh3Flag = sSpecEntry.substring(BEH3_FLAG_POS, BEH3_FLAG_POS + 1);

            if ((sHistFlag.equals(sHist)) && (sBeh3Flag.equals(sBeh)))
            {
                bIsFound = true;
                result.setHistology(sSpecEntry.substring(HIST2_FLAG_POS, HIST2_FLAG_POS + 4));
                result.setBehavior(sSpecEntry.substring(BEH2_FLAG_POS, BEH2_FLAG_POS + 1));
            }
        }

        return 0;
    }


    /**
     * For ICD-O-3 histology & behavior with Site flag turned on, checks site and returns the ICD-O-2 histology and behavior.
     * @param sSite ICD-O-3 site number
     * @param iHist ICD-O-3 histology number
     * @param sBeh ICD-O-3 behavior
     * @param result corresponding ICD-O-2 conversion entry.
     */
    private static int ConvertSite(String sSite, int iHist, String sBeh, IcdConversionEntry result)
    {
        int iFlagsSet = FLAG_NONE;
        int iSite = Integer.parseInt(sSite.substring(1, 3).trim());

        //Since there are so few cases, these are hard coded.
        String sBehFirstDigit = sBeh.substring(0, 0);
        if (iHist == 8240 && sBehFirstDigit.equals("1"))
        {
            if (iSite != 181)
                result.setHistology("8241");
            else
                iFlagsSet = FLAG_HAND_REVIEW;
        }

        if (iHist == 8245 && sBehFirstDigit.equals("1"))
        {
            result.setHistology("8240");
            iFlagsSet = FLAG_HAND_REVIEW;
            if (iSite == 181)
                result.setBehavior("1");
            else
                result.setBehavior("3");
        }

        if (iHist == 8249 && sBehFirstDigit.equals("3") && iSite == 181)
            result.setBehavior("1");

        if (iHist == 9133 && sBehFirstDigit.equals("3") && (iSite >= 340 && iSite <= 349))
        {
            result.setHistology("9134");
            result.setBehavior("1");
            iFlagsSet = FLAG_HAND_REVIEW;
        }

        if (iHist == 9160 && sBehFirstDigit.equals("0"))
        {
            iFlagsSet = FLAG_HAND_REVIEW;
            if (iSite >= 440 && iSite <= 449)
                result.setHistology("8724");
        }

        if (iHist == 9590 && sBehFirstDigit.equals("3") && (iSite >= 710 && iSite <= 719))
        {
            result.setHistology("9594");
            iFlagsSet = FLAG_HAND_REVIEW;
        }
        return iFlagsSet;
    }


    /*
        //Number of lines to search in each table. Taken from 'char MorphTable[2000][8] ='
        //  where the first [x] is the number of lines and the 2nd [y] is the line length with null.
        //static const int MORPH_LINES       = 2000;    //index based, not searched
        static const int MORPH_SPECIAL_LINES = 82;

        //Other constants and bookkeeping variables.
        static const char FLAG_ON     = '1';
        static const int  NO_FLAGS    = 0;
        static const int  FLAG_SET    = 1;
        static const int  HAND_REVIEW = 2;
        static const int  iTRUE       = 0;
        static const int  iFALSE      = 1;

        enum eFlags {HandReview, InvalidSite, InvalidHist, InvalidBeh};


        //----------------------------------------------------------------------------------------
        //  This set of functions is called thru Convert() by an external source,
        //  Information required from the user is
        //     ICD-O-3 Site (length 4 char string)
        //     ICD-O-3 Histology (length 4 char string)
        //     ICD-O-3 Behavior (length 1 char string)
        //  Using these data, the function retrieves the corresponding ICD-O-2 Histology and
        //  Behavior.  It also generates a hand review needed flag and 3 Invalid Flags (to
        //  note that an invalid code was provided). For Site, this is an 'Out of range' flag.
        //  Codes that are between C000 and C810 are considered 'valid' even if they are not
        //  specifically listed in the ICD-O-3 book.  These data are sent back to the user in
        //  the referenced strings passed into Convert.  Therefore, the user also needs to send
        //      ICD-O-2 Histology (length 4 char string - modified by Convert() )
        //      ICD-O-2 Behavior  (length 1 char string - modified by Convert() )
        //      Flags             (length 4 char string - modified by Convert() )
        //          Hand Review Needed, Invalid Site, Invalid Histology, Invalid Behavior
        //  This function also returns an integer to signify the level of confidence of
        //  the conversion.
        //      0 - Conversion completed, no invalid flags, no need for hand review.
        //      1 - Conversion had at least 1 invalid flag turned on.
        //      2 - Conversion completed, no invalid flag, but suggests hand review.
        //
        //  The main function accesses the other conversion functions as needed.  These
        //  functions should NOT be accessed separately.
        //
        //  Nicki Schussler    2/13/2001
        //----------------------------------------------------------------------------------------


        //---------------------------------------------------------
        //Convert - given the ICD-O-3 information, retrieves the converted ICD-O-2 hist & beh
        //  and sets the appropriate flags.  It places these data into the provided strings.
        int Convert(char *sSite, char *sHist, char *sBeh,
                    char *sICDO2Hist, char *sICDO2Beh, char *sFlags)
        {
           int iFlagsSet = NO_FLAGS, iHist;

           try
           {
              //Initialize return strings.
              if (strlen(sICDO2Hist) == 4)
                 strcpy(sICDO2Hist, "9999");
              else
                 ICDGenerateException("ICD-O-2 Histology string is not length 4", "TConvert");

              if (strlen(sICDO2Beh) == 1)
                 strcpy(sICDO2Beh, "9");
              else
                 ICDGenerateException("ICD-O-2 Behavior string is not length 1", "TConvert");

              if (strlen(sFlags) == 4)
                 strcpy(sFlags, "0000");
              else
                 ICDGenerateException("Flags string is not length 4", "TConvert");

              if (CheckInvalid(sSite, sHist, sBeh, sFlags) == 0)
                 {
                 /////////////////////////////////////////////////////////////////////////
                 // Conversion function calls:
                 //   ConvertMorph:      Search by Hist.  Provides base ICD-O-2 Histology.
                 //                      Assumes behavior is unchanged.  If hand review flag
                 //                      is not 'X', set Hand Review='1'.  If Beh flag is not
                 //                      'X', calls ConvertMorphSpec.  If site flag is not
                 //                      'X', calls ConvertSite.
                 //   ConvertMorphSpec:  Search by Hist/Beh. Called if Beh flag is not 'X'.
                 //   ConvertSite:       Site specific conversions. Hard coded.  Called if
                 //                      Site flag is not 'X'.
                 //
                 /////////////////////////////////////////////////////////////////////////

                 iHist = atoi(sHist);
                 ConvertMorph(sSite, iHist, sBeh, sICDO2Hist, sICDO2Beh, sFlags);
                 }

                // based on flags, set return value. Are there invalid flags,
                // if not, is there a hand review flag, if not, return 0.
                if (!StrEqual(&sFlags[1], "000"))
                    iFlagsSet = FLAG_SET;
                else if (sFlags[HandReview] == FLAG_ON)
                    iFlagsSet = HAND_REVIEW;
                else
                    iFlagsSet = NO_FLAGS;     //no invalid codes & does not need hand review
           }
           catch (ICDException & x)
           {
               x.AddCallpath("Convert()","TConvert");
               throw;
           }

           return iFlagsSet;
       }
       //---------------------------------------------------------

       //CheckInvalid - checks the incoming ICD-O-3 codes for values which are out of range.
       //  Sets appropriate flags and returns 1 if any of the flags have been turned on.
       int CheckInvalid(char *sSite, char *sHist, char *sBeh, char *sFlags)
       {
           int iFlagsSet = NO_FLAGS;
           int iSite, iHist;

           try
           {
               // Check for invalid data being entered (Out of range, not specific codes)
               //Site out of range
               if ((sSite[0] != 'C' && sSite[0] != 'c') || (IsNumeric(&sSite[1], 3) == iFALSE))
                   sFlags[InvalidSite] = FLAG_ON;
               else
               {
                   iSite = atoi(&sSite[1]);
                   if (iSite < 000 || iSite > 809)
                        sFlags[InvalidSite] = FLAG_ON;
               }

               //Hist out of range
               if (IsNumeric(sHist, 4) == iFALSE)
                   sFlags[InvalidHist] = FLAG_ON;
               else
               {
               iHist = atoi(sHist);
               if (iHist < 8000 || iHist > 9999)
                    sFlags[InvalidHist] = FLAG_ON;
               }

                //Behavior invalid
                if (!memchr("012369", sBeh[0], strlen("012369")))
                    sFlags[InvalidBeh] = FLAG_ON;

                // set value of return to 1 if any flags were set
                if (!StrEqual(&sFlags[1], "000"))
                    iFlagsSet = FLAG_SET;
            }
            catch (ICDException & x)
            {
                x.AddCallpath("CheckInvalid()","TConvert");
                throw;
            }

            return iFlagsSet;
        }


        //ConvertMorph - takes ICD-O-3 Hist code and retrieves the most common ICD-O-2 Hist
        //  code.  Assumes that Beh remains the same.  Also retrieves the hand review flag,
        //  the behavior specific conversion flag (which may cause ConvertMorphSpec to be called)
        //  and the site specific conversion flag (which may cause ConvertSite to be called)
        //    FLAG values:
        //    0-3, 6, 9 -> this behavior is sent to next function
        //    A         -> all behaviors have flag set
        //    X         -> Invalid morph, not in ICD-O-3
        void ConvertMorph(char *sSite, int iHist, char *sBeh, char *sICDO2Hist, char *sICDO2Beh, char *sFlags)
        {
            try
            {
                char sMorphEntry[8];
                enum eStart {HistO2, HandFlag=4, BehFlag, SiteFlag};

                strcpy(sMorphEntry, MorphTable[iHist-8000]);

                //is this an invalid Histology?
                if (StrEqual(sMorphEntry, "9999XXX"))
                    sFlags[InvalidHist] = FLAG_ON;
                else
                {
                  //Get standard ICD-O-2 hist and beh
                  strncpy(sICDO2Hist, &sMorphEntry[HistO2], 4);       sICDO2Hist[4] = '\0';
                  sICDO2Beh[0] = sBeh[0];

                  //Set hand review flag if needed
                  if (sMorphEntry[HandFlag] == 'A' || sMorphEntry[HandFlag] == sBeh[0])
                  sFlags[HandReview] = FLAG_ON;
                  if (iHist == 8402 && sBeh[0] == '3')
                  sFlags[HandReview] = FLAG_ON;

                  //Is there a special behavior based conversion?
                  if (sMorphEntry[BehFlag] == sBeh[0] || sMorphEntry[BehFlag] == 'A')
                  ConvertMorphSpec(iHist, sBeh, sICDO2Hist, sICDO2Beh);

                  //Is there a special site based conversion?
                  if (sMorphEntry[SiteFlag] == sBeh[0])
                  ConvertSite(sSite, iHist, sBeh, sICDO2Hist, sICDO2Beh, sFlags);
              }
          }
          catch (ICDException & x)
          {
              x.AddCallpath("ConvertMorph()","TConvert");
              throw;
          }
        }


        //ConvertMorphSpec - takes an ICD-O-3 Hist & Behavior and retrieves the
        //  ICD-O-2 Hist and Behavior.
        int  ConvertMorphSpec(int iHist, char *sBeh, char *sICDO2Hist, char *sICDO2Beh)
        {
            int     iIsFound = iFALSE;

            try
            {
                int     i;
                char *  sSpecEntry, sHist[5];
                enum    eStart {Hist3, Beh3=4, Hist2, Beh2=9};

                //itoa(iHist, sHist, 10);
                sprintf(sHist,"%d",iHist);

                for (i=0; i < MORPH_SPECIAL_LINES && iIsFound == iFALSE; i++)
                {
                    sSpecEntry = MorphSpecialTable[i];
                    if (strncmp(&sSpecEntry[Hist3], sHist, 4) == 0 &&
                        strncmp(&sSpecEntry[Beh3], sBeh, 1) == 0)
                    {
                        iIsFound = iTRUE;
                        strncpy(sICDO2Hist, &sSpecEntry[Hist2], 4);
                        strncpy(sICDO2Beh,  &sSpecEntry[Beh2], 1);
                        sICDO2Hist[4] = '\0';
                        sICDO2Beh[1] = '\0';
                    }
                }
            }
            catch (ICDException & x)
            {
                x.AddCallpath("ConvertMorphSpec()","TConvert");
                throw;
            }

            return iIsFound;
        }


        //ConvertSite - for ICD-O-3 histology & behavior with Site flag turned on, checks site
        //  and returns the ICD-O-2 histology and behavior.
        void ConvertSite(char *sSite, int iHist, char *sBeh, char *sICDO2Hist, char *sICDO2Beh, char *sFlags)
        {
            int iSite;

            try
            {
                iSite = atoi(&sSite[1]);

                //Since there are so few cases, these are hard coded.
                if (iHist == 8240 && sBeh[0] == '1')
                {
                    if (iSite != 181)
                        strcpy(sICDO2Hist, "8241");
                    else
                        sFlags[HandReview] = FLAG_ON;
                }

                if (iHist == 8245 && sBeh[0] == '1')
                {
                    strcpy(sICDO2Hist, "8240");
                    sFlags[HandReview] = FLAG_ON;
                    if (iSite == 181)
                    sICDO2Beh[0] = '1';
                    else
                    sICDO2Beh[0] = '3';
                }

                if (iHist == 8249 && sBeh[0] == '3' && iSite == 181)
                    sICDO2Beh[0] = '1';

                if (iHist == 9133 && sBeh[0] == '3' && (iSite >= 340 && iSite <= 349))
                {
                    strcpy(sICDO2Hist, "9134");
                    sICDO2Beh[0] = '1';
                    sFlags[HandReview] = FLAG_ON;
                }

                if (iHist == 9160 && sBeh[0] == '0')
                {
                    sFlags[HandReview] = FLAG_ON;
                    if (iSite >= 440 && iSite <= 449)
                    strcpy(sICDO2Hist, "8724");
                }

                if (iHist == 9590 && sBeh[0] == '3' && (iSite >= 710 && iSite <= 719))
                {
                    strcpy(sICDO2Hist, "9594");
                    sFlags[HandReview] = FLAG_ON;
                }
            }
            catch (ICDException & x)
            {
                x.AddCallpath("ConvertSite()","TConvert");
                throw;
            }
        }


        //UTILITY FUNCTION
        //IsNumeric - is sString a string of numeric characters of length
        //            iLength (does not include null terminater)?
        int  IsNumeric(char *sString, int iLength)
        {
            int iIsNumeric = iTRUE, i;

            if ((signed) strlen(sString) != iLength)
                iIsNumeric = iFALSE;

            for (i=0; i < iLength && iIsNumeric == iTRUE; i++)
            {
                if (isdigit(sString[i]) == 0)
                iIsNumeric = iFALSE;
            }

            return iIsNumeric;
        }

        */






















}
