package br.com.msappointment.api.appointment.model;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum AppointmentTypeEnum {
    FIRST_VACCINE("First Vaccine"),
    FIRST_BATH_FREE("First Free Bath"),
    INITIAL_CHECKUP("Initial Checkup"),
    VACCINATIONS("Vaccinations"),
    BATH_AND_GROOMING("Bath and Grooming"),
    VETERINARY_CONSULTATIONS("Veterinary Consultations"),
    MEDICATION_ADMINISTRATION("Medication Administration"),
    DENTAL_CLEANING("Dental Cleaning"),
    NAIL_TRIMMING("Nail Trimming"),
    EAR_CLEANING("Ear Cleaning"),
    FLEA_TREATMENT("Flea Treatment"),
    DEWORMING("Deworming"),
    MICROCHIPPING("Microchipping"),
    SPAY_NEUTER("Spay/Neuter"),
    DIETARY_COUNSELING("Dietary Counseling"),
    BEHAVIORAL_TRAINING("Behavioral Training"),
    PHYSICAL_THERAPY("Physical Therapy"),
    GROOMING("Grooming"),
    ALLERGY_TREATMENT("Allergy Treatment"),
    WEIGHT_MANAGEMENT("Weight Management"),
    SENIOR_PET_CARE("Senior Pet Care"),
    PUPPY_KITTEN_CARE("Puppy/Kitten Care"),
    EMERGENCY_CARE("Emergency Care"),
    SURGERY("Surgery"),
    BLOOD_TESTS("Blood Tests"),
    URINE_TESTS("Urine Tests"),
    X_RAYS("X-Rays"),
    ULTRASOUND("Ultrasound"),
    HEARTWORM_TESTING("Heartworm Testing"),
    TICK_PREVENTION("Tick Prevention");

    private final String description;

    AppointmentTypeEnum(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    @JsonCreator
    public static AppointmentTypeEnum fromString(String value) {
        for (AppointmentTypeEnum type : AppointmentTypeEnum.values()) {
            if (type.name().equalsIgnoreCase(value)) { // Verifica pelo nome do ENUM
                return type;
            }
        }
        throw new IllegalArgumentException("Invalid care type: " + value);
    }
}