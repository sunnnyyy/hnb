package com.hnb.enums;

public enum ExpenseType {
	RENT,
    FOOD,
    DHOBI,
    SALARY,
    WI_FI,
    BREAK_FAST,
    lUNCH,
    DINNER,
    TRAVEL,
    SAFAI_ROOM,
    HOTEL_EXPENSE,
    SAFAI_BATHROOM,
    ELECTRICITY_BILL,
    HOTEL_MAINTENANCE,
    OTHER
//    	
//    public String toDisplayString() {
//        String name = this.name(); // Get enum constant name
//        String[] words = name.split("_"); // Split by underscores
//        StringBuilder displayName = new StringBuilder();
//
//        for (String word : words) {
//            // Capitalize first letter and lowercase the rest
//            displayName.append(word.charAt(0))
//                       .append(word.substring(1).toLowerCase())
//                       .append(" ");
//        }
//
//        return displayName.toString().trim(); // Remove trailing space
//    }
}
