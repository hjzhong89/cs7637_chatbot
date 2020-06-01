package models.component;

/**
 * Enumeration of component type statuses
 */
public enum RequirementStatus {
    MISSING("MISSING"), // The component is not present in the sentence
    INCOMPLETE("INCOMPLETE"), // The component was found in the sentence, but data is missing
    ENTITY_MISSING("ENTITY_MISSING"),
    COMPLETE("COMPLETE"); // The component was found and has all data needed to fulfill the intent

    private String name;

    RequirementStatus(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}
