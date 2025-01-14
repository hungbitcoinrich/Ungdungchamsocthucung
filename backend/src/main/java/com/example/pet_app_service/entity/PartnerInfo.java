package com.example.pet_app_service.entity;

import com.example.pet_app_service.service.ServiceType;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalTime;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@Entity
public class PartnerInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String businessLicense;

    @Column
    private String businessCode;

    @Column(nullable = false)
    private String businessName;

    @Column(nullable = false)
    private String address;

    @Column(nullable = false)
    private LocalTime openingTime;

    @Column(nullable = false)
    private LocalTime closingTime;

    @Column(nullable = false)
    private Double latitude;

    @Column(nullable = false)
    private Double longitude;

    @Column
    private double averageRating;

    @Column(nullable = false)
    private boolean isOpen = true; // Mặc định là dịch vụ mở

    // Hàm kiểm tra trạng thái cửa hàng (đang mở hay đóng)
    public void updateIsOpenStatus() {
        LocalTime currentTime = LocalTime.now();
        if (!isOpen) {
            this.isOpen = false;  // Đảm bảo cửa hàng đóng
        } else {
            // Kiểm tra nếu giờ hiện tại nằm trong khung giờ mở cửa và không bị đóng dịch vụ sớm
            this.isOpen = !currentTime.isBefore(openingTime) && !currentTime.isAfter(closingTime);
        }
    }

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private ServiceCategory serviceCategory; // Loại dịch vụ

    @ElementCollection(targetClass = ServiceType.class)
    @Enumerated(EnumType.STRING)
    private Set<ServiceType> services = new HashSet<>(); // Các dịch vụ do đối tác cung cấp

    @Lob
    private String imageUrl;

    @Enumerated(EnumType.STRING)
    private PartnerStatus status = PartnerStatus.PENDING;

    @OneToOne
    @JoinColumn(name = "user_id")
    @JsonBackReference
    private User user;

    // Hàm set dịch vụ theo loại dịch vụ đã chọn
    public void setAvailableServicesByCategory() {
        // Nếu loại dịch vụ là chăm sóc thú cưng
        if (this.serviceCategory == ServiceCategory.PET_CARE) {
            this.services.add(ServiceType.PET_BOARDING); // Có thể chọn thêm dịch vụ này
            this.services.add(ServiceType.PET_SPA); // Có thể chọn thêm dịch vụ này
            this.services.add(ServiceType.PET_GROOMING); // Có thể chọn thêm dịch vụ này
            this.services.add(ServiceType.PET_WALKING); // Có thể chọn thêm dịch vụ này
        }
        // Nếu loại dịch vụ là phòng khám thú y
        else if (this.serviceCategory == ServiceCategory.VETERINARY_CARE) {
            this.services.add(ServiceType.VETERINARY_EXAMINATION); // Có thể chọn thêm dịch vụ này
            this.services.add(ServiceType.VACCINATION); // Có thể chọn thêm dịch vụ này
            this.services.add(ServiceType.SURGERY); // Có thể chọn thêm dịch vụ này
            this.services.add(ServiceType.REGULAR_CHECKUP); // Có thể chọn thêm dịch vụ này
        }
    }

    public enum PartnerStatus {
        PENDING,
        APPROVED,
        REJECTED
    }

    public enum ServiceCategory {
        PET_CARE,     // Dịch vụ chăm sóc thú cưng
        VETERINARY_CARE // Dịch vụ phòng khám thú y
    }

    public double getAverageRating() {
        return averageRating;
    }

    public void setAverageRating(double averageRating) {
        this.averageRating = averageRating;
    }

    public Boolean getIsOpen() {
        return isOpen; // Default to true if null
    }

    public void setIsOpen(Boolean isOpen) {
        this.isOpen = isOpen;
    }

    // Phương thức để đối tác đóng cửa dịch vụ sớm
    public void closeServiceEarly() {
        this.isOpen = false;
    }

    // Phương thức để đối tác mở lại dịch vụ
    public void reopenService() {
        // Kiểm tra nếu dịch vụ bị đóng thủ công (isOpen = false)
        if (!isOpen) {
            // Mở lại dịch vụ, trạng thái giờ mở và đóng cửa sẽ được tính lại
            this.isOpen = true;
            updateIsOpenStatus(); // Tính lại trạng thái dựa trên giờ mở cửa và đóng cửa
        }
    }
}
