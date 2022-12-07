package task2.entity;

/**
 * It is enumeration with different driving violation types
 * which can be in .json input file
 *
 * Program has to count fines total sum
 * For every driving violation type in input files
 */
public enum DrivingViolationType {
    SPEEDING,
    VEHICLE_USAGE_VIOLATION,
    ROAD_SIGN_IGNORING,
    ROAD_ACCIDENT,
    DRIVING_WHILE_INTOXICATED,
    SEAT_BELT_UNPLUGGED,
    PARKING_RULES_VIOLATION,
    DRIVING_WITHOUT_LICENSE,
    DANGEROUS_DRIVING
}
