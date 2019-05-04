/*
 * Copyright (C) 2019 Information Management Services, Inc.
 */
package com.imsweb.algorithms;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import com.imsweb.algorithms.napiia.NapiiaInputPatientDto;
import com.imsweb.algorithms.napiia.NapiiaInputRecordDto;
import com.imsweb.algorithms.napiia.NapiiaResultsDto;
import com.imsweb.algorithms.napiia.NapiiaUtils;
import com.imsweb.algorithms.nhia.NhiaInputPatientDto;
import com.imsweb.algorithms.nhia.NhiaInputRecordDto;
import com.imsweb.algorithms.nhia.NhiaResultsDto;
import com.imsweb.algorithms.nhia.NhiaUtils;

import static com.imsweb.algorithms.nhia.NhiaUtils.NHIA_OPTION_ALL_CASES;
import static com.imsweb.algorithms.nhia.NhiaUtils.NHIA_OPTION_SEVEN_AND_NINE;
import static com.imsweb.algorithms.nhia.NhiaUtils.NHIA_OPTION_SEVEN_ONLY;

// TODO FD move the static creation of the algorithms to each individual utility class...
public class Algorithms {

    public static final String ALG_NHIA = "nhia";
    public static final String ALG_NAPIIA = "napiia";

    public static final String FIELD_TUMORS = "tumors";

    public static final String FIELD_SPAN_HISP_OR = "spanishHispanicOrigin";
    public static final String FIELD_NAME_LAST = "nameLast";
    public static final String FIELD_NAME_FIRST = "nameFirst";
    public static final String FIELD_NAME_MAIDEN = "nameMaiden";
    public static final String FIELD_COUNTRY_BIRTH = "birthplaceCountry";
    public static final String FIELD_RACE1 = "race1";
    public static final String FIELD_RACE2 = "race2";
    public static final String FIELD_RACE3 = "race3";
    public static final String FIELD_RACE4 = "race4";
    public static final String FIELD_RACE5 = "race5";
    public static final String FIELD_SEX = "sex";
    public static final String FIELD_IHS = "ihsLink";
    public static final String FIELD_COUNTY_DX = "countyAtDx";
    public static final String FIELD_STATE_DX = "addrAtDxState";
    public static final String FIELD_NHIA = "nhiaDerivedHispOrigin";
    public static final String FIELD_NAPIIA = "napiiaValue";
    public static final String FIELD_NAPIIA_NEEDS_REVIEW = "napiiaNeedsHumanReview";
    public static final String FIELD_NAPIIA_REVIEW_REASON = "napiiaReasonForReview";

    public static final String PARAM_NHIA_OPTION = "nhiaOption";

    private static Map<String, AlgorithmField> _CACHED_FIELDS = new HashMap<>();

    static {
        // standard fields
        addField(_CACHED_FIELDS, AlgorithmField.of(FIELD_NAME_LAST, 2230, "TODO", 50));
        addField(_CACHED_FIELDS, AlgorithmField.of(FIELD_NAME_FIRST, 2240, "TODO", 50));
        addField(_CACHED_FIELDS, AlgorithmField.of(FIELD_NAME_MAIDEN, 2390, "TODO", 50));
        addField(_CACHED_FIELDS, AlgorithmField.of(FIELD_COUNTRY_BIRTH, 254, "TODO", 3));
        addField(_CACHED_FIELDS, AlgorithmField.of(FIELD_SEX, 220, "TODO", 1));
        addField(_CACHED_FIELDS, AlgorithmField.of(FIELD_RACE1, 160, "TODO", 2));
        addField(_CACHED_FIELDS, AlgorithmField.of(FIELD_RACE2, 161, "TODO", 2));
        addField(_CACHED_FIELDS, AlgorithmField.of(FIELD_RACE3, 162, "TODO", 2));
        addField(_CACHED_FIELDS, AlgorithmField.of(FIELD_RACE4, 163, "TODO", 2));
        addField(_CACHED_FIELDS, AlgorithmField.of(FIELD_RACE5, 164, "TODO", 2));
        addField(_CACHED_FIELDS, AlgorithmField.of(FIELD_SPAN_HISP_OR, 190, "Spanish/Hispanic Origin", 1));
        addField(_CACHED_FIELDS, AlgorithmField.of(FIELD_IHS, 192, "TODO", 1));
        addField(_CACHED_FIELDS, AlgorithmField.of(FIELD_NHIA, 191, "TODO", 1));
        addField(_CACHED_FIELDS, AlgorithmField.of(FIELD_NAPIIA, 193, "TODO", 2));
        addField(_CACHED_FIELDS, AlgorithmField.of(FIELD_COUNTY_DX, 90, "TODO", 3));
        addField(_CACHED_FIELDS, AlgorithmField.of(FIELD_STATE_DX, 80, "TODO", 2));

        // non-standard fields
        addField(_CACHED_FIELDS, AlgorithmField.of(FIELD_NAPIIA_NEEDS_REVIEW, 25001, "TODO", 1));
        addField(_CACHED_FIELDS, AlgorithmField.of(FIELD_NAPIIA_REVIEW_REASON, 25002, "TODO", 256));
    }

    private static void addField(Map<String, AlgorithmField> cache, AlgorithmField field) {
        cache.put(field.getId(), field);
    }

    private static Map<String, Algorithm> _CACHED_ALGORITHMS = new HashMap<>();

    private static ReentrantReadWriteLock _LOCK = new ReentrantReadWriteLock();

    public static void initialize() {
        _LOCK.writeLock().lock();
        try {
            addAlgorithm(_CACHED_ALGORITHMS, createAlgorithmNhia());
            addAlgorithm(_CACHED_ALGORITHMS, createAlgorithmNaiia());
        }
        finally {
            _LOCK.writeLock().unlock();
        }
    }

    private static void addAlgorithm(Map<String, Algorithm> cache, Algorithm algorithm) {
        cache.put(algorithm.getId(), algorithm);
    }

    public static void registerAlgorithm(Algorithm algorithm) {

        // TODO validate what can be validated...

        _LOCK.writeLock().lock();
        try {
            _CACHED_ALGORITHMS.put(algorithm.getId(), algorithm);
        }
        finally {
            _LOCK.writeLock().unlock();
        }
    }

    public static List<Algorithm> getAlgorithms() {
        _LOCK.readLock().lock();
        try {
            return new ArrayList<>(_CACHED_ALGORITHMS.values());
        }
        finally {
            _LOCK.readLock().unlock();
        }
    }

    public static Algorithm getAlgorithm(String algorithmId) {
        _LOCK.readLock().lock();
        try {
            if (_CACHED_ALGORITHMS.isEmpty())
                throw new RuntimeException("Algorithms have not been initialized!");
            Algorithm algorithm = _CACHED_ALGORITHMS.get(algorithmId);
            if (algorithm == null)
                throw new RuntimeException("Unable to get algorithm " + algorithmId);
            return algorithm;
        }
        finally {
            _LOCK.readLock().unlock();
        }
    }

    private static Map<String, Object> extratPatient(AlgorithmInput input) {
        return input.getPatient() == null ? Collections.emptyMap() : input.getPatient();
    }

    @SuppressWarnings("unused")
    private static List<Map<String, Object>> extractTumors(Map<String, Object> patient) {
        return extractTumors(patient, false);
    }

    @SuppressWarnings("unchecked")
    private static List<Map<String, Object>> extractTumors(Map<String, Object> patient, boolean createTumorIfEmpty) {
        List<Map<String, Object>> tumors = (List<Map<String, Object>>)patient.get(Algorithms.FIELD_TUMORS);
        if (tumors == null)
            tumors = new ArrayList<>();
        if (tumors.isEmpty() && createTumorIfEmpty)
            tumors.add(new HashMap<>());
        return tumors;
    }

    private static Algorithm createAlgorithmNhia() {
        return new Algorithm() {

            @Override
            public String getId() {
                return ALG_NHIA;
            }

            @Override
            public String getName() {
                return NhiaUtils.ALG_NAME;
            }

            @Override
            public String getVersion() {
                return NhiaUtils.ALG_VERSION;
            }

            @Override
            public String getInfo() {
                return NhiaUtils.ALG_INFO;
            }

            @Override
            public List<AlgorithmParam> getParameters() {
                List<AlgorithmParam> params = new ArrayList<>();
                params.add(AlgorithmParam.of(PARAM_NHIA_OPTION, "NHIA Option", String.class, Arrays.asList(NHIA_OPTION_ALL_CASES, NHIA_OPTION_SEVEN_AND_NINE, NHIA_OPTION_SEVEN_ONLY)));
                return params;
            }

            @Override
            public List<AlgorithmField> getInputFields() {
                List<AlgorithmField> fields = new ArrayList<>();
                fields.add(_CACHED_FIELDS.get(FIELD_SPAN_HISP_OR));
                fields.add(_CACHED_FIELDS.get(FIELD_NAME_LAST));
                fields.add(_CACHED_FIELDS.get(FIELD_NAME_MAIDEN));
                fields.add(_CACHED_FIELDS.get(FIELD_COUNTRY_BIRTH));
                fields.add(_CACHED_FIELDS.get(FIELD_RACE1));
                fields.add(_CACHED_FIELDS.get(FIELD_SEX));
                fields.add(_CACHED_FIELDS.get(FIELD_IHS));
                fields.add(_CACHED_FIELDS.get(FIELD_COUNTY_DX));
                fields.add(_CACHED_FIELDS.get(FIELD_STATE_DX));
                return fields;
            }

            @Override
            public List<AlgorithmField> getOutputFields() {
                List<AlgorithmField> fields = new ArrayList<>();
                fields.add(_CACHED_FIELDS.get(FIELD_NHIA));
                return fields;
            }

            @Override
            public AlgorithmOutput execute(AlgorithmInput input) {

                Map<String, Object> patientMap = extratPatient(input);

                List<NhiaInputRecordDto> list = new ArrayList<>();
                for (Map<String, Object> tumorMap : extractTumors(patientMap, true)) {
                    NhiaInputRecordDto dto = new NhiaInputRecordDto();
                    dto.setSpanishHispanicOrigin((String)patientMap.get(FIELD_SPAN_HISP_OR));
                    dto.setBirthplaceCountry((String)patientMap.get(FIELD_COUNTRY_BIRTH));
                    dto.setSex((String)patientMap.get(FIELD_SEX));
                    dto.setRace1((String)patientMap.get(FIELD_RACE1));
                    dto.setIhs((String)patientMap.get(FIELD_IHS));
                    dto.setNameLast((String)patientMap.get(FIELD_NAME_LAST));
                    dto.setNameMaiden((String)patientMap.get(FIELD_NAME_MAIDEN));
                    dto.setCountyAtDx((String)tumorMap.get(FIELD_COUNTY_DX));
                    dto.setStateAtDx((String)tumorMap.get(FIELD_STATE_DX));
                    list.add(dto);
                }

                NhiaInputPatientDto inputPatient = new NhiaInputPatientDto();
                inputPatient.setNhiaInputPatientDtoList(list);

                NhiaResultsDto result = NhiaUtils.computeNhia(inputPatient, (String)input.getParameters().get(PARAM_NHIA_OPTION));

                Map<String, Object> patientOutput = new HashMap<>();
                patientOutput.put(FIELD_NHIA, result.getNhia());

                AlgorithmOutput output = new AlgorithmOutput();
                output.setPatient(patientOutput);

                return output;
            }
        };
    }

    private static Algorithm createAlgorithmNaiia() {
        return new Algorithm() {

            @Override
            public String getId() {
                return ALG_NAPIIA;
            }

            @Override
            public String getName() {
                return NapiiaUtils.ALG_NAME;
            }

            @Override
            public String getVersion() {
                return NapiiaUtils.ALG_VERSION;
            }

            @Override
            public String getInfo() {
                return NapiiaUtils.ALG_INFO;
            }

            @Override
            public List<AlgorithmParam> getParameters() {
                return Collections.emptyList();
            }

            @Override
            public List<AlgorithmField> getInputFields() {
                List<AlgorithmField> fields = new ArrayList<>();
                fields.add(_CACHED_FIELDS.get(FIELD_SPAN_HISP_OR));
                fields.add(_CACHED_FIELDS.get(FIELD_NAME_LAST));
                fields.add(_CACHED_FIELDS.get(FIELD_NAME_MAIDEN));
                fields.add(_CACHED_FIELDS.get(FIELD_NAME_FIRST));
                fields.add(_CACHED_FIELDS.get(FIELD_COUNTRY_BIRTH));
                fields.add(_CACHED_FIELDS.get(FIELD_RACE1));
                fields.add(_CACHED_FIELDS.get(FIELD_RACE2));
                fields.add(_CACHED_FIELDS.get(FIELD_RACE3));
                fields.add(_CACHED_FIELDS.get(FIELD_RACE4));
                fields.add(_CACHED_FIELDS.get(FIELD_RACE5));
                fields.add(_CACHED_FIELDS.get(FIELD_SEX));
                return fields;
            }

            @Override
            public List<AlgorithmField> getOutputFields() {
                List<AlgorithmField> fields = new ArrayList<>();
                fields.add(_CACHED_FIELDS.get(FIELD_NAPIIA));
                fields.add(_CACHED_FIELDS.get(FIELD_NAPIIA_NEEDS_REVIEW));
                fields.add(_CACHED_FIELDS.get(FIELD_NAPIIA_REVIEW_REASON));
                return fields;
            }

            @Override
            public AlgorithmOutput execute(AlgorithmInput input) {

                Map<String, Object> patientMap = extratPatient(input);

                List<NapiiaInputRecordDto> list = new ArrayList<>();
                for (Map<String, Object> ignored : extractTumors(patientMap, true)) {
                    NapiiaInputRecordDto dto = new NapiiaInputRecordDto();
                    dto.setSpanishHispanicOrigin((String)patientMap.get(FIELD_SPAN_HISP_OR));
                    dto.setBirthplaceCountry((String)patientMap.get(FIELD_COUNTRY_BIRTH));
                    dto.setSex((String)patientMap.get(FIELD_SEX));
                    dto.setRace1((String)patientMap.get(FIELD_RACE1));
                    dto.setRace2((String)patientMap.get(FIELD_RACE2));
                    dto.setRace3((String)patientMap.get(FIELD_RACE3));
                    dto.setRace4((String)patientMap.get(FIELD_RACE4));
                    dto.setRace5((String)patientMap.get(FIELD_RACE5));
                    dto.setNameLast((String)patientMap.get(FIELD_NAME_LAST));
                    dto.setNameMaiden((String)patientMap.get(FIELD_NAME_MAIDEN));
                    dto.setNameFirst((String)patientMap.get(FIELD_NAME_FIRST));
                    list.add(dto);
                }

                NapiiaInputPatientDto inputPatient = new NapiiaInputPatientDto();
                inputPatient.setNapiiaInputPatientDtoList(list);

                NapiiaResultsDto result = NapiiaUtils.computeNapiia(inputPatient);

                Map<String, Object> patientOutput = new HashMap<>();
                patientOutput.put(FIELD_NAPIIA, result.getNapiiaValue());
                patientOutput.put(FIELD_NAPIIA_NEEDS_REVIEW, Boolean.TRUE.equals(result.getNeedsHumanReview()) ? "1" : "0");
                patientOutput.put(FIELD_NAPIIA_REVIEW_REASON, result.getReasonForReview());

                AlgorithmOutput output = new AlgorithmOutput();
                output.setPatient(patientOutput);

                return output;
            }
        };
    }
}
