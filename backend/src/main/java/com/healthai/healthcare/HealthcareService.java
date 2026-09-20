package com.healthai.healthcare;

import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class HealthcareService {

    private final List<HealthcareFacilityDto> facilityDatabase = new ArrayList<>();

    public HealthcareService() {
        initializeFacilities();
    }

    private void initializeFacilities() {
        // Core hospitals & clinics around metropolitan coordinates (default center: 28.5672, 77.2100)
        facilityDatabase.add(new HealthcareFacilityDto(
                1L,
                "All India Institute of Medical Sciences (AIIMS) & Trauma Center",
                "Apex Teaching Hospital & Emergency",
                List.of("Emergency Medicine / Critical Care", "Cardiologist", "Pulmonologist", "Neurologist", "General Physician", "General Surgeon / Emergency Medicine", "Oncologist", "Pediatrician"),
                "Sri Aurobindo Marg, Ansari Nagar",
                "New Delhi",
                "+91 11 2658 8500 / 112",
                4.9,
                28.5672,
                77.2100,
                true,
                "24/7 Emergency & Level-1 Trauma",
                0.0
        ));

        facilityDatabase.add(new HealthcareFacilityDto(
                2L,
                "Fortis Escorts Heart Institute",
                "Super-Specialty Cardiac Hospital",
                List.of("Cardiologist", "Cardiologist / Emergency Medicine", "Emergency Medicine / Cardiologist", "Vascular Surgeon"),
                "Okhla Road, New Friends Colony",
                "New Delhi",
                "+91 11 4713 5000",
                4.8,
                28.5614,
                77.2798,
                true,
                "24/7 Emergency & Cath Lab",
                0.0
        ));

        facilityDatabase.add(new HealthcareFacilityDto(
                3L,
                "National Institute of Tuberculosis & Respiratory Diseases",
                "Pulmonary & Chest Specialty Hospital",
                List.of("Pulmonologist", "Pulmonologist / Infectious Disease", "General Physician / Pulmonologist", "Infectious Disease Specialist"),
                "Sri Aurobindo Marg, Near Qutub Minar",
                "New Delhi",
                "+91 11 2651 7834",
                4.7,
                28.5255,
                77.1856,
                true,
                "24/7 Pulmonary Emergency",
                0.0
        ));

        facilityDatabase.add(new HealthcareFacilityDto(
                4L,
                "Max Super Speciality Hospital",
                "Multi-Specialty Hospital & Trauma Unit",
                List.of("Emergency Medicine / Critical Care", "Cardiologist", "Neurologist", "Orthopedic", "Gastroenterologist", "Dermatologist"),
                "1, 2, Press Enclave Road, Saket",
                "New Delhi",
                "+91 11 2651 5050",
                4.8,
                28.5283,
                77.2120,
                true,
                "24/7 Emergency & Diagnostics",
                0.0
        ));

        facilityDatabase.add(new HealthcareFacilityDto(
                5L,
                "Apollo Hospitals & Indraprastha Clinical Centre",
                "Multi-Specialty Tertiary Hospital",
                List.of("Cardiologist", "Neurologist", "Gastroenterologist", "Endocrinologist", "Pediatrician", "General Physician"),
                "Delhi-Mathura Road, Sarita Vihar",
                "New Delhi",
                "+91 11 2692 5858",
                4.8,
                28.5367,
                77.2965,
                true,
                "24/7 Emergency Services",
                0.0
        ));

        facilityDatabase.add(new HealthcareFacilityDto(
                6L,
                "Dermatology & Skin Allergy Speciality Clinic",
                "Specialized Outpatient Clinic",
                List.of("Dermatologist", "Dermatologist / Allergist", "Dermatologist / Venereologist"),
                "B-42 Ring Road, South Extension Part 1",
                "New Delhi",
                "+91 11 2465 1122",
                4.6,
                28.5723,
                77.2215,
                false,
                "09:00 AM - 08:00 PM",
                0.0
        ));

        facilityDatabase.add(new HealthcareFacilityDto(
                7L,
                "Comprehensive Gastroenterology & Liver Institute",
                "Gastrointestinal Specialty Clinic",
                List.of("Gastroenterologist", "Hepatologist / Gastroenterologist", "Gastroenterologist / Oncologist"),
                "Sector D, Pocket 1, Vasant Kunj",
                "New Delhi",
                "+91 11 4630 0000",
                4.7,
                28.5190,
                77.1560,
                false,
                "08:00 AM - 08:00 PM",
                0.0
        ));

        facilityDatabase.add(new HealthcareFacilityDto(
                8L,
                "Urban Primary Health Centre & Family Practice",
                "Community Health & Primary Clinic",
                List.of("General Physician", "Pediatrician", "General Physician / Nutritionist", "ENT Specialist / General Physician"),
                "Main Market Complex, Hauz Khas",
                "New Delhi",
                "+91 11 2686 3344",
                4.5,
                28.5494,
                77.2001,
                false,
                "08:30 AM - 09:30 PM",
                0.0
        ));

        facilityDatabase.add(new HealthcareFacilityDto(
                9L,
                "Brain & Spine Neurological Care Centre",
                "Neurosciences Specialist Clinic",
                List.of("Neurologist", "Neurologist / Neurosurgeon", "Neurologist / Sleep Specialist"),
                "Plot 22, Institutional Area, Lodhi Road",
                "New Delhi",
                "+91 11 2469 8899",
                4.9,
                28.5900,
                77.2270,
                true,
                "24/7 Stroke Unit & Emergency",
                0.0
        ));

        facilityDatabase.add(new HealthcareFacilityDto(
                10L,
                "Mother & Child Care Maternity Hospital",
                "Obstetrics & Gynecology Centre",
                List.of("Gynecologist", "Gynecologist / Obstetrician", "Pediatrician", "Gynecologist / High-Risk Obstetrician"),
                "C-15 Malviya Nagar, Near Corner Market",
                "New Delhi",
                "+91 11 2668 1122",
                4.7,
                28.5350,
                77.2060,
                true,
                "24/7 Labor & Delivery Emergency",
                0.0
        ));
    }

    public List<HealthcareFacilityDto> findNearbyFacilities(
            Double userLat,
            Double userLng,
            String specialty,
            Double radiusKm,
            Boolean emergencyOnly,
            String query) {

        double originLat = (userLat != null) ? userLat : 28.5672;
        double originLng = (userLng != null) ? userLng : 77.2100;
        double maxRadius = (radiusKm != null && radiusKm > 0) ? radiusKm : 50.0;

        return facilityDatabase.stream()
                .map(facility -> {
                    double dist = calculateHaversineDistanceKm(originLat, originLng, facility.getLatitude(), facility.getLongitude());
                    facility.setDistanceKm(Math.round(dist * 10.0) / 10.0);
                    return facility;
                })
                .filter(facility -> facility.getDistanceKm() <= maxRadius)
                .filter(facility -> {
                    if (Boolean.TRUE.equals(emergencyOnly)) {
                        return Boolean.TRUE.equals(facility.getEmergency24x7());
                    }
                    return true;
                })
                .filter(facility -> {
                    if (specialty == null || specialty.isBlank() || "ALL".equalsIgnoreCase(specialty)) {
                        return true;
                    }
                    String sLower = specialty.toLowerCase().trim();
                    return facility.getSpecialties().stream()
                            .anyMatch(spec -> spec.toLowerCase().contains(sLower) || sLower.contains(spec.toLowerCase()));
                })
                .filter(facility -> {
                    if (query == null || query.isBlank()) {
                        return true;
                    }
                    String qLower = query.toLowerCase().trim();
                    return facility.getName().toLowerCase().contains(qLower) ||
                            facility.getAddress().toLowerCase().contains(qLower) ||
                            facility.getType().toLowerCase().contains(qLower);
                })
                .sorted(Comparator.comparingDouble(HealthcareFacilityDto::getDistanceKm))
                .collect(Collectors.toList());
    }

    public List<String> getAvailableSpecialties() {
        Set<String> specialties = new TreeSet<>();
        for (HealthcareFacilityDto f : facilityDatabase) {
            specialties.addAll(f.getSpecialties());
        }
        return new ArrayList<>(specialties);
    }

    public Optional<HealthcareFacilityDto> getFacilityById(Long id) {
        return facilityDatabase.stream()
                .filter(f -> f.getId().equals(id))
                .findFirst();
    }

    /**
     * Calculates the great-circle distance between two points in kilometers using the Haversine formula.
     */
    public static double calculateHaversineDistanceKm(double lat1, double lon1, double lat2, double lon2) {
        final double EARTH_RADIUS_KM = 6371.0;

        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);

        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(dLon / 2) * Math.sin(dLon / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return EARTH_RADIUS_KM * c;
    }
}
