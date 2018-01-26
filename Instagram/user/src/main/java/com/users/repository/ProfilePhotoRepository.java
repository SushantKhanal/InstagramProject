package com.users.repository;

import com.users.model.ProfilePhoto;
import com.users.model.UserPhotos;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProfilePhotoRepository extends JpaRepository<ProfilePhoto,Long> {
    @Query("SELECT p from ProfilePhoto p where p.user.username=:username and p.photoStatus=:photoStatus" )
    public ProfilePhoto getProfilePhotoByUserNameAndStatus(@Param("username") String username,
                                                      @Param("photoStatus") Character photoStatus);

    @Query("SELECT p from ProfilePhoto p where p.user.username=:username" )
    public ProfilePhoto getProfilePhotoByUserUsername(@Param("username") String username);
}

