import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * A self-contained, in-memory LMS prototype. Open this file in IntelliJ IDEA
 * and run LMSApplication.main(). No external libraries or project setup are required.
 */
public class LMSApplication extends JFrame {
    private static final Color NAVY = new Color(26, 39, 68);
    private static final Color BLUE = new Color(62, 112, 226);
    private static final Color BG = new Color(245, 247, 251);
    private static final List<User> USERS = new ArrayList<>();
    private static final List<Course> COURSES = new ArrayList<>();
    private static final List<String[]> ACTIVITY = new ArrayList<>();

    private final JComboBox<String> rolePicker = new JComboBox<>(new String[]{"Admin", "Instructor", "Student"});
    private final JLabel pageTitle = new JLabel();
    private final JPanel content = new JPanel(new BorderLayout());

    public LMSApplication() {
        super("LearnSphere | Learning Management System");
        seedData();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(1050, 700));
        setSize(1200, 780);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        add(buildHeader(), BorderLayout.NORTH);
        content.setBackground(BG);
        add(content, BorderLayout.CENTER);
        showDashboard();
    }

    private JPanel buildHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(NAVY);
        header.setBorder(new EmptyBorder(15, 24, 15, 24));
        JLabel brand = new JLabel("LearnSphere");
        brand.setForeground(Color.WHITE);
        brand.setFont(new Font("SansSerif", Font.BOLD, 22));
        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        right.setOpaque(false);
        JLabel label = new JLabel("View dashboard as");
        label.setForeground(new Color(220, 226, 240));
        rolePicker.setFocusable(false);
        rolePicker.addActionListener(e -> showDashboard());
        right.add(label);
        right.add(rolePicker);
        header.add(brand, BorderLayout.WEST);
        header.add(right, BorderLayout.EAST);
        return header;
    }

    private void showDashboard() {
        String role = (String) rolePicker.getSelectedItem();
        pageTitle.setText(role + " Dashboard");
        pageTitle.setFont(new Font("SansSerif", Font.BOLD, 26));
        pageTitle.setForeground(NAVY);
        JPanel page = new JPanel(new BorderLayout(0, 18));
        page.setBackground(BG);
        page.setBorder(new EmptyBorder(25, 28, 24, 28));
        page.add(pageTitle, BorderLayout.NORTH);
        JTabbedPane tabs = new JTabbedPane();
        tabs.setFont(new Font("SansSerif", Font.PLAIN, 14));
        if ("Admin".equals(role)) buildAdminTabs(tabs);
        else if ("Instructor".equals(role)) buildInstructorTabs(tabs);
        else buildStudentTabs(tabs);
        page.add(tabs, BorderLayout.CENTER);
        content.removeAll();
        content.add(page, BorderLayout.CENTER);
        content.revalidate();
        content.repaint();
    }

    private void buildAdminTabs(JTabbedPane tabs) {
        tabs.addTab("Overview", overviewPanel("System overview", new String[]{"Users", "Courses", "Enrollments", "Completion"},
                new String[]{String.valueOf(USERS.size()), String.valueOf(COURSES.size()), "384", "78%"},
                "Platform activity and performance at a glance."));
        tabs.addTab("Users", userPanel());
        tabs.addTab("Courses", coursePanel(true));
        tabs.addTab("Analytics", analyticsPanel());
        tabs.addTab("Settings", settingsPanel());
        tabs.addTab("Activity", activityPanel());
    }

    private void buildInstructorTabs(JTabbedPane tabs) {
        tabs.addTab("Overview", overviewPanel("Teaching overview", new String[]{"My courses", "Learners", "To grade", "Avg. progress"},
                new String[]{"4", "126", "8", "72%"}, "Keep track of courses, student engagement, and grading."));
        tabs.addTab("My courses", coursePanel(false));
        tabs.addTab("Assignment grading", gradingPanel());
        tabs.addTab("Student performance", performancePanel());
        tabs.addTab("Enrollment stats", analyticsPanel());
    }

    private void buildStudentTabs(JTabbedPane tabs) {
        tabs.addTab("My learning", overviewPanel("Welcome back, Alex", new String[]{"Enrolled courses", "Completed lessons", "Assignments due", "Average grade"},
                new String[]{"3", "18", "2", "88%"}, "Your learning progress and next steps."));
        tabs.addTab("Course catalog", catalogPanel());
        tabs.addTab("Materials", materialsPanel());
        tabs.addTab("Assignments", studentAssignmentsPanel());
        tabs.addTab("Progress & grades", studentProgressPanel());
    }

    private JPanel overviewPanel(String heading, String[] labels, String[] values, String subtitle) {
        JPanel panel = basePanel();
        panel.setLayout(new BorderLayout(0, 22));
        panel.add(sectionHeading(heading, subtitle), BorderLayout.NORTH);
        JPanel cards = new JPanel(new GridLayout(1, labels.length, 14, 14));
        cards.setOpaque(false);
        for (int i = 0; i < labels.length; i++) cards.add(metricCard(labels[i], values[i]));
        JPanel center = new JPanel(new BorderLayout(0, 22));
        center.setOpaque(false);
        center.add(cards, BorderLayout.NORTH);
        center.add(infoPanel("Recent updates", "Your dashboard is ready. Use the tabs above to manage courses, learners, assignments, and reports."), BorderLayout.CENTER);
        panel.add(center, BorderLayout.CENTER);
        return panel;
    }

    private JPanel userPanel() {
        JPanel panel = basePanel();
        panel.setLayout(new BorderLayout(0, 14));
        panel.add(sectionHeading("User management", "Create accounts and manage roles across the platform."), BorderLayout.NORTH);
        String[] cols = {"Name", "Email", "Role", "Status"};
        DefaultTableModel model = model(cols);
        for (User u : USERS) model.addRow(new Object[]{u.name, u.email, u.role, u.status});
        JTable table = table(model);
        JPanel actions = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        actions.setOpaque(false);
        JButton add = primaryButton("Add user");
        JButton edit = button("Edit selected");
        JButton delete = button("Delete selected");
        actions.add(add); actions.add(edit); actions.add(delete);
        add.addActionListener(e -> {
            JTextField name = new JTextField(); JTextField email = new JTextField();
            JComboBox<String> role = new JComboBox<>(new String[]{"Student", "Instructor", "Admin"});
            if (form("New user", new String[]{"Full name", "Email address", "Role"}, new JComponent[]{name, email, role})) {
                if (name.getText().isBlank() || email.getText().isBlank()) { message("Name and email are required."); return; }
                USERS.add(new User(name.getText().trim(), email.getText().trim(), (String) role.getSelectedItem(), "Active"));
                log("User created", name.getText().trim() + " was added as " + role.getSelectedItem());
                showDashboard();
            }
        });
        edit.addActionListener(e -> {
            int row = table.getSelectedRow(); if (row < 0) { message("Select a user first."); return; }
            int idx = table.convertRowIndexToModel(row); User u = USERS.get(idx);
            JTextField name = new JTextField(u.name); JTextField email = new JTextField(u.email);
            JComboBox<String> role = new JComboBox<>(new String[]{"Student", "Instructor", "Admin"}); role.setSelectedItem(u.role);
            if (form("Edit user", new String[]{"Full name", "Email address", "Role"}, new JComponent[]{name, email, role})) {
                u.name = name.getText().trim(); u.email = email.getText().trim(); u.role = (String) role.getSelectedItem();
                log("User updated", u.name + " account was updated"); showDashboard();
            }
        });
        delete.addActionListener(e -> {
            int row = table.getSelectedRow(); if (row < 0) { message("Select a user first."); return; }
            int idx = table.convertRowIndexToModel(row); String name = USERS.get(idx).name;
            USERS.remove(idx); log("User deleted", name + " account was removed"); showDashboard();
        });
        panel.add(actions, BorderLayout.SOUTH);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        return panel;
    }

    private JPanel coursePanel(boolean admin) {
        JPanel panel = basePanel();
        panel.setLayout(new BorderLayout(0, 14));
        panel.add(sectionHeading(admin ? "Course management" : "My courses", admin ? "Create, update, and remove courses." : "Manage course content and course details."), BorderLayout.NORTH);
        DefaultTableModel model = model(new String[]{"Course", "Instructor", "Category", "Enrolled", "Status"});
        for (Course c : COURSES) model.addRow(new Object[]{c.title, c.instructor, c.category, c.enrolled, c.status});
        JTable table = table(model);
        JPanel actions = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0)); actions.setOpaque(false);
        JButton add = primaryButton("Create course"); JButton edit = button("Edit selected"); JButton delete = button("Delete selected");
        actions.add(add); actions.add(edit); actions.add(delete);
        add.addActionListener(e -> createCourse());
        edit.addActionListener(e -> {
            int row = table.getSelectedRow(); if (row < 0) { message("Select a course first."); return; }
            Course c = COURSES.get(table.convertRowIndexToModel(row)); editCourse(c);
        });
        delete.addActionListener(e -> {
            int row = table.getSelectedRow(); if (row < 0) { message("Select a course first."); return; }
            Course c = COURSES.get(table.convertRowIndexToModel(row)); COURSES.remove(c);
            log("Course deleted", c.title + " was removed"); showDashboard();
        });
        panel.add(actions, BorderLayout.SOUTH); panel.add(new JScrollPane(table), BorderLayout.CENTER);
        return panel;
    }

    private void createCourse() {
        JTextField title = new JTextField(); JTextField instructor = new JTextField("Morgan Lee");
        JTextField category = new JTextField(); JTextArea description = new JTextArea(4, 24); description.setLineWrap(true);
        if (form("Create course", new String[]{"Title", "Instructor", "Category", "Description / syllabus"}, new JComponent[]{title, instructor, category, new JScrollPane(description)})) {
            if (title.getText().isBlank()) { message("Course title is required."); return; }
            COURSES.add(new Course(title.getText().trim(), instructor.getText().trim(), category.getText().trim(), 0, "Published"));
            log("Course created", title.getText().trim() + " is now available"); showDashboard();
        }
    }

    private void editCourse(Course c) {
        JTextField title = new JTextField(c.title); JTextField instructor = new JTextField(c.instructor);
        JTextField category = new JTextField(c.category); JComboBox<String> status = new JComboBox<>(new String[]{"Published", "Draft", "Archived"}); status.setSelectedItem(c.status);
        if (form("Edit course", new String[]{"Title", "Instructor", "Category", "Status"}, new JComponent[]{title, instructor, category, status})) {
            c.title = title.getText().trim(); c.instructor = instructor.getText().trim(); c.category = category.getText().trim(); c.status = (String) status.getSelectedItem();
            log("Course updated", c.title + " details were updated"); showDashboard();
        }
    }

    private JPanel catalogPanel() {
        JPanel panel = basePanel(); panel.setLayout(new BorderLayout(0, 14));
        panel.add(sectionHeading("Explore courses", "Browse available learning and enroll in a course."), BorderLayout.NORTH);
        DefaultTableModel model = model(new String[]{"Course", "Instructor", "Category", "Learners", "Status"});
        for (Course c : COURSES) model.addRow(new Object[]{c.title, c.instructor, c.category, c.enrolled, c.status});
        JTable table = table(model); JButton enroll = primaryButton("Enroll in selected course");
        enroll.addActionListener(e -> {
            int row = table.getSelectedRow(); if (row < 0) { message("Select a course first."); return; }
            Course c = COURSES.get(table.convertRowIndexToModel(row)); c.enrolled++;
            log("Student enrolled", "Alex Morgan enrolled in " + c.title); message("You are enrolled in " + c.title + "."); showDashboard();
        });
        panel.add(new JScrollPane(table), BorderLayout.CENTER); panel.add(enroll, BorderLayout.SOUTH); return panel;
    }

    private JPanel gradingPanel() {
        JPanel panel = basePanel(); panel.setLayout(new BorderLayout(0, 14));
        panel.add(sectionHeading("Assignment grading", "Review submissions and provide grades and feedback."), BorderLayout.NORTH);
        DefaultTableModel model = model(new String[]{"Student", "Course", "Assignment", "Submitted", "Grade", "Feedback"});
        model.addRow(new Object[]{"Alex Morgan", "Java Fundamentals", "Loops exercise", "Today", "Pending", "—"});
        model.addRow(new Object[]{"Sam Patel", "Web Design Basics", "Landing page", "Yesterday", "82", "Good structure"});
        model.addRow(new Object[]{"Taylor Kim", "Data Analysis", "Chart project", "Oct 7", "Pending", "—"});
        JTable table = table(model); JButton grade = primaryButton("Grade selected submission");
        grade.addActionListener(e -> {
            int row = table.getSelectedRow(); if (row < 0) { message("Select a submission first."); return; }
            int idx = table.convertRowIndexToModel(row); JTextField score = new JTextField(); JTextArea feedback = new JTextArea(3, 24); feedback.setLineWrap(true);
            if (form("Grade submission", new String[]{"Grade (0–100)", "Feedback"}, new JComponent[]{score, new JScrollPane(feedback)})) {
                model.setValueAt(score.getText().trim(), idx, 4); model.setValueAt(feedback.getText().trim(), idx, 5);
                log("Assignment graded", model.getValueAt(idx, 0) + " received feedback"); message("Grade and feedback saved.");
            }
        });
        panel.add(new JScrollPane(table), BorderLayout.CENTER); panel.add(grade, BorderLayout.SOUTH); return panel;
    }

    private JPanel performancePanel() {
        JPanel panel = basePanel(); panel.setLayout(new BorderLayout(0, 14));
        panel.add(sectionHeading("Student performance", "Progress across your courses."), BorderLayout.NORTH);
        DefaultTableModel model = model(new String[]{"Student", "Course", "Lessons complete", "Assignments", "Average", "Progress"});
        model.addRow(new Object[]{"Alex Morgan", "Java Fundamentals", "8 / 10", "3 / 4", "91%", "80%"});
        model.addRow(new Object[]{"Sam Patel", "Web Design Basics", "6 / 8", "4 / 4", "86%", "75%"});
        model.addRow(new Object[]{"Taylor Kim", "Data Analysis", "5 / 12", "2 / 5", "78%", "42%"});
        panel.add(new JScrollPane(table(model)), BorderLayout.CENTER); return panel;
    }

    private JPanel analyticsPanel() {
        JPanel panel = basePanel(); panel.setLayout(new BorderLayout(0, 18));
        panel.add(sectionHeading("Performance analytics", "Enrollment, completion, and learner outcomes."), BorderLayout.NORTH);
        JPanel body = new JPanel(new GridLayout(1, 2, 18, 0)); body.setOpaque(false);
        body.add(chartCard("Course enrollment", new String[]{"Java Fundamentals", "Web Design Basics", "Data Analysis", "UX Research"}, new int[]{82, 64, 48, 35}));
        body.add(chartCard("Course completion", new String[]{"Java Fundamentals", "Web Design Basics", "Data Analysis", "UX Research"}, new int[]{76, 68, 54, 81}));
        panel.add(body, BorderLayout.CENTER);
        return panel;
    }

    private JPanel chartCard(String title, String[] names, int[] values) {
        JPanel card = whiteCard(); card.setLayout(new BorderLayout(0, 12));
        JLabel heading = new JLabel(title); heading.setFont(new Font("SansSerif", Font.BOLD, 16)); heading.setForeground(NAVY);
        JPanel bars = new JPanel(new GridLayout(names.length, 1, 0, 10)); bars.setOpaque(false);
        for (int i = 0; i < names.length; i++) {
            JPanel line = new JPanel(new BorderLayout(8, 4)); line.setOpaque(false);
            JLabel name = new JLabel(names[i]); name.setPreferredSize(new Dimension(145, 24));
            JProgressBar bar = new JProgressBar(0, 100); bar.setValue(values[i]); bar.setStringPainted(true); bar.setString(values[i] + "%"); bar.setForeground(BLUE); bar.setBackground(new Color(232, 237, 246));
            line.add(name, BorderLayout.WEST); line.add(bar, BorderLayout.CENTER); bars.add(line);
        }
        card.add(heading, BorderLayout.NORTH); card.add(bars, BorderLayout.CENTER); return card;
    }

    private JPanel settingsPanel() {
        JPanel panel = basePanel(); panel.setLayout(new BorderLayout(0, 14));
        panel.add(sectionHeading("System settings", "Manage platform-wide preferences."), BorderLayout.NORTH);
        JPanel settings = whiteCard(); settings.setLayout(new GridLayout(0, 1, 8, 8));
        JCheckBox registration = new JCheckBox("Allow new student registrations", true);
        JCheckBox notifications = new JCheckBox("Send assignment deadline reminders", true);
        JCheckBox certificates = new JCheckBox("Issue certificates when courses are completed", true);
        JComboBox<String> timezone = new JComboBox<>(new String[]{"Asia/Kolkata", "UTC", "America/New_York", "Europe/London"});
        JPanel tz = new JPanel(new FlowLayout(FlowLayout.LEFT)); tz.setOpaque(false); tz.add(new JLabel("Default time zone")); tz.add(timezone);
        settings.add(registration); settings.add(notifications); settings.add(certificates); settings.add(tz);
        JButton save = primaryButton("Save settings"); save.addActionListener(e -> { log("Settings updated", "System preferences were saved"); message("Settings saved successfully."); });
        JPanel wrap = new JPanel(new BorderLayout(0, 12)); wrap.setOpaque(false); wrap.add(settings, BorderLayout.CENTER); wrap.add(save, BorderLayout.SOUTH);
        panel.add(wrap, BorderLayout.CENTER); return panel;
    }

    private JPanel activityPanel() {
        JPanel panel = basePanel(); panel.setLayout(new BorderLayout(0, 14));
        panel.add(sectionHeading("System activity", "Recent changes across the LMS."), BorderLayout.NORTH);
        DefaultTableModel model = model(new String[]{"Event", "Details"});
        for (int i = ACTIVITY.size() - 1; i >= 0; i--) model.addRow(ACTIVITY.get(i));
        panel.add(new JScrollPane(table(model)), BorderLayout.CENTER); return panel;
    }

    private JPanel materialsPanel() {
        JPanel panel = basePanel(); panel.setLayout(new BorderLayout(0, 14));
        panel.add(sectionHeading("Course materials", "Open the learning resources for your enrolled courses."), BorderLayout.NORTH);
        DefaultTableModel model = model(new String[]{"Course", "Material", "Type", "Action"});
        model.addRow(new Object[]{"Java Fundamentals", "Week 1 — Getting started", "PDF", "Available"});
        model.addRow(new Object[]{"Java Fundamentals", "Loops and conditions", "Video", "Available"});
        model.addRow(new Object[]{"Web Design Basics", "HTML & CSS reference", "PDF", "Available"});
        JTable table = table(model); JButton open = primaryButton("Open selected material");
        open.addActionListener(e -> { if (table.getSelectedRow() < 0) message("Select a material first."); else message("Material opened in the course content viewer."); });
        panel.add(new JScrollPane(table), BorderLayout.CENTER); panel.add(open, BorderLayout.SOUTH); return panel;
    }

    private JPanel studentAssignmentsPanel() {
        JPanel panel = basePanel(); panel.setLayout(new BorderLayout(0, 14));
        panel.add(sectionHeading("Assignments", "Submit your work and check submission status."), BorderLayout.NORTH);
        DefaultTableModel model = model(new String[]{"Assignment", "Course", "Due date", "Status", "Grade"});
        model.addRow(new Object[]{"Loops exercise", "Java Fundamentals", "Oct 16", "Not submitted", "—"});
        model.addRow(new Object[]{"Landing page", "Web Design Basics", "Oct 18", "Submitted", "86"});
        JTable table = table(model); JButton submit = primaryButton("Submit assignment");
        submit.addActionListener(e -> {
            int row = table.getSelectedRow(); if (row < 0) { message("Select an assignment first."); return; }
            int idx = table.convertRowIndexToModel(row); JFileChooser chooser = new JFileChooser(); chooser.setDialogTitle("Choose your assignment file");
            if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                model.setValueAt("Submitted", idx, 3); log("Assignment submitted", "Alex Morgan submitted " + model.getValueAt(idx, 0)); message("Submission received: " + chooser.getSelectedFile().getName());
            }
        });
        panel.add(new JScrollPane(table), BorderLayout.CENTER); panel.add(submit, BorderLayout.SOUTH); return panel;
    }

    private JPanel studentProgressPanel() {
        JPanel panel = basePanel(); panel.setLayout(new BorderLayout(0, 14));
        panel.add(sectionHeading("Progress and grades", "Track course completion and review instructor feedback."), BorderLayout.NORTH);
        JPanel body = new JPanel(new GridLayout(0, 1, 0, 12)); body.setOpaque(false);
        body.add(progressCard("Java Fundamentals", 80, "91% average grade"));
        body.add(progressCard("Web Design Basics", 75, "86% average grade"));
        body.add(progressCard("Data Analysis", 42, "78% average grade"));
        panel.add(body, BorderLayout.CENTER); return panel;
    }

    private JPanel progressCard(String course, int progress, String grade) {
        JPanel card = whiteCard(); card.setLayout(new BorderLayout(12, 8));
        JLabel title = new JLabel(course); title.setFont(new Font("SansSerif", Font.BOLD, 15)); title.setForeground(NAVY);
        JLabel pct = new JLabel(progress + "% complete  •  " + grade);
        JProgressBar bar = new JProgressBar(0, 100); bar.setValue(progress); bar.setStringPainted(true); bar.setString(progress + "% complete"); bar.setForeground(BLUE);
        card.add(title, BorderLayout.NORTH); card.add(bar, BorderLayout.CENTER); card.add(pct, BorderLayout.SOUTH); return card;
    }

    private JPanel sectionHeading(String title, String subtitle) {
        JPanel p = new JPanel(new GridLayout(0, 1, 0, 5)); p.setOpaque(false);
        JLabel t = new JLabel(title); t.setFont(new Font("SansSerif", Font.BOLD, 19)); t.setForeground(NAVY);
        JLabel s = new JLabel(subtitle); s.setForeground(new Color(105, 115, 132)); s.setFont(new Font("SansSerif", Font.PLAIN, 13));
        p.add(t); p.add(s); return p;
    }

    private JPanel metricCard(String label, String value) {
        JPanel card = whiteCard(); card.setLayout(new GridLayout(2, 1, 0, 7));
        JLabel l = new JLabel(label); l.setForeground(new Color(105, 115, 132));
        JLabel v = new JLabel(value); v.setForeground(NAVY); v.setFont(new Font("SansSerif", Font.BOLD, 28));
        card.add(l); card.add(v); return card;
    }   

    private JPanel infoPanel(String title, String detail) {
        JPanel p = whiteCard(); p.setLayout(new BorderLayout(0, 10));
        JLabel h = new JLabel(title); h.setForeground(NAVY); h.setFont(new Font("SansSerif", Font.BOLD, 16));
        JTextArea d = new JTextArea(detail); d.setWrapStyleWord(true); d.setLineWrap(true); d.setEditable(false); d.setOpaque(false); d.setForeground(new Color(90, 100, 118)); d.setFont(new Font("SansSerif", Font.PLAIN, 14));
        p.add(h, BorderLayout.NORTH); p.add(d, BorderLayout.CENTER); return p;
    }

    private JPanel basePanel() { JPanel p = new JPanel(new BorderLayout()); p.setBackground(BG); p.setBorder(new EmptyBorder(20, 18, 18, 18)); return p; }
    private JPanel whiteCard() { JPanel p = new JPanel(); p.setBackground(Color.WHITE); p.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(new Color(226, 231, 240)), new EmptyBorder(17, 18, 17, 18))); return p; }
    private JButton primaryButton(String text) { JButton b = new JButton(text); b.setBackground(BLUE); b.setForeground(Color.WHITE); b.setFocusPainted(false); return b; }
    private JButton button(String text) { JButton b = new JButton(text); b.setFocusPainted(false); return b; }
    private JTable table(DefaultTableModel model) { JTable t = new JTable(model); t.setRowHeight(30); t.setFillsViewportHeight(true); t.setSelectionMode(ListSelectionModel.SINGLE_SELECTION); t.getTableHeader().setReorderingAllowed(false); return t; }
    private DefaultTableModel model(String[] cols) { return new DefaultTableModel(cols, 0) { public boolean isCellEditable(int r, int c) { return false; } }; }

    private boolean form(String title, String[] labels, JComponent[] fields) {
        JPanel p = new JPanel(new GridLayout(labels.length, 2, 10, 10));
        for (int i = 0; i < labels.length; i++) { p.add(new JLabel(labels[i])); p.add(fields[i]); }
        return JOptionPane.showConfirmDialog(this, p, title, JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE) == JOptionPane.OK_OPTION;
    }
    private void message(String text) { JOptionPane.showMessageDialog(this, text, "LearnSphere", JOptionPane.INFORMATION_MESSAGE); }
    private void log(String event, String details) { ACTIVITY.add(new String[]{event, details}); }

    private static void seedData() {
        if (!USERS.isEmpty()) return;
        USERS.add(new User("Alex Morgan", "alex@example.com", "Student", "Active"));
        USERS.add(new User("Morgan Lee", "morgan@example.com", "Instructor", "Active"));
        USERS.add(new User("Jamie Chen", "jamie@example.com", "Admin", "Active"));
        COURSES.add(new Course("Java Fundamentals", "Morgan Lee", "Programming", 82, "Published"));
        COURSES.add(new Course("Web Design Basics", "Morgan Lee", "Design", 64, "Published"));
        COURSES.add(new Course("Data Analysis", "Priya Shah", "Data", 48, "Published"));
        COURSES.add(new Course("UX Research", "Morgan Lee", "Design", 35, "Draft"));
        ACTIVITY.add(new String[]{"System started", "LearnSphere LMS prototype is ready"});
        ACTIVITY.add(new String[]{"New enrollment", "8 learners enrolled this week"});
        ACTIVITY.add(new String[]{"Assignment graded", "Web Design Basics submissions reviewed"});
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); } catch (Exception ignored) { }
            new LMSApplication().setVisible(true);
        });
    }

    private static class User {
        String name, email, role, status;
        User(String name, String email, String role, String status) { this.name = name; this.email = email; this.role = role; this.status = status; }
    }
    private static class Course {
        String title, instructor, category, status; int enrolled;
        Course(String title, String instructor, String category, int enrolled, String status) { this.title = title; this.instructor = instructor; this.category = category; this.enrolled = enrolled; this.status = status; }
    }
}
