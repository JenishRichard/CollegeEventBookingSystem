package com.collegeevent.util;

import com.collegeevent.model.Admin;
import com.collegeevent.localisation.MessageService;
import com.collegeevent.model.Staff;
import com.collegeevent.model.Student;
import com.collegeevent.model.User;

public class UserUtils {

    public static String describeUser(User user) {
        return switch (user) {
            case Admin admin -> "Admin account: " + admin.getName();
            case Student student -> "Student account: " + student.getName();
            case Staff staff -> "Staff account: " + staff.getName();
        };
    }

    public static String describeUser(User user, MessageService messageService) {
        return switch (user) {
            case Admin admin -> messageService.getMessage("user.admin.account", admin.getName());
            case Student student -> messageService.getMessage("user.student.account", student.getName());
            case Staff staff -> messageService.getMessage("user.staff.account", staff.getName());
        };
    }
}
