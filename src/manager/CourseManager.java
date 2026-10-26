package manager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import model.Course;

/**
 * Lop CourseManager: Quan ly danh sach Mon hoc.
 * Phu trach chinh: Nguoi 2 (Mon hoc & danh gia) - Ca test: D01, D07, D11.
 * 
 * Quy tac nghiep vu:
 * - Ma mon duy nhat, ten khong rong, tin chi nguyen 1-6, trim khoang trang dau/cuoi.
 * - Khong cho phep sua tin chi hay loai mon sau khi da tao ket qua.
 */
public class CourseManager {
    private List<Course> courses;

    public CourseManager() {
        this.courses = new ArrayList<>();
    }

    /**
     * Them mon hoc moi vao he thong.
     * @param course Doi tuong Course (TheoryCourse hoac PracticeCourse).
     * @return true neu them thanh cong.
     */
    public boolean addCourse(Course course) {
        if (course == null) {
            throw new IllegalArgumentException("Doi tuong mon hoc khong duoc null.");
        }

        String courseId = course.getCourseId();
        if (this.findCourse(courseId) != null) {
            throw new IllegalArgumentException("Loi: Ma mon hoc [" + courseId + "] da ton tai trong he thong.");
        }

        return this.courses.add(course);
    }

    /**
     * Tim mon hoc theo ma mon (courseId).
     */
    public Course findCourse(String courseId) {
        if (courseId == null || courseId.trim().isEmpty()) {
            return null;
        }
        String cleanId = courseId.trim();
        for (Course c : this.courses) {
            if (c.getCourseId().equalsIgnoreCase(cleanId)) {
                return c;
            }
        }
        return null;
    }

    /**
     * Lay danh sach toan bo mon hoc.
     */
    public List<Course> getAllCourses() {
        return Collections.unmodifiableList(this.courses);
    }

    /**
     * So luong mon hoc hien co.
     */
    public int getCourseCount() {
        return this.courses.size();
    }
}
