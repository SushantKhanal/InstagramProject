package com.users.serviceImpl;

import com.users.dto.ImageListdto;
import com.users.dto.LikeActiondto;
import com.users.dto.UserPhotodto;
import com.users.dto.UserPostDto;
import com.users.exceptionHandler.NoFollowingException;
import com.users.model.User;
import com.users.model.UserPhotos;
import com.users.repository.LikesRepository;
import com.users.repository.PhotoRepository;
import com.users.repository.UserRepository;
import com.users.service.FollowService;
import com.users.service.LikesService;
import com.users.service.PhotoService;
import com.users.service.UserService;
import com.users.utils.PhotoUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.List;

@Service
@Transactional
public class PhotoServiceImpl implements PhotoService {

    @Resource
    private PhotoRepository photoRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private PhotoService photoService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private FollowService followService;

    @Autowired
    private LikesRepository likesRepository;

    @Autowired
    private LikesService likesService;

    @Autowired
    private EntityManager entityManager;

    public void savePhoto(UserPhotodto userPhotodto) {
        File dir = new File(System.getProperty("catalina.home") + "/uploads");
        if (!dir.exists()) {
            dir.mkdir();
        }
        User user = userService.getUser(userPhotodto.getUsername());
        List<ImageListdto> listOfImages = userPhotodto.getImageList();
        for (ImageListdto s : listOfImages) {
            byte[] imageDecoded = Base64.getDecoder().decode(s.getImageName());
            String filename = imageDecoded.toString();
            String pathToImage = dir + "/" + filename;
            try {
                FileOutputStream fout = new FileOutputStream(pathToImage);
                fout.write(imageDecoded);
                fout.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            UserPhotos userPhotos = new UserPhotos();
            userPhotos.setUser(user);
            userPhotos.setCreated_date(new Date());

            userPhotos.setCaption(s.getCaption());
            userPhotos.setImage_path(filename);
            photoRepository.save(userPhotos);
        }
    }

    public List<UserPostDto> getPosts(String userName, Pageable pageable) {
        User user = userRepository.getUserByUsername(userName);
        String sql = "SELECT f.following_userId ,f.userId FROM " +
                "follow f WHERE f.userId = :id AND f.isFollowing=TRUE ";
        Query followUserQuery = entityManager.createNativeQuery(sql).setParameter("id", user.getId());
        List<Object[]> listOfFollowedUser = followUserQuery.getResultList();
        if (listOfFollowedUser == null || listOfFollowedUser.size()==0) {
            throw new NoFollowingException("Follow others to see their Posts", "Not following anyone");
        } else {
            final String SQL_QUERY =
                    "SELECT u.username,t2.profile_pic,t.image_path,t.created_date,t.caption,f.following_userId,uAt.activationStatus " +
                            "FROM photo_table t " +
                            "LEFT JOIN user_table u ON t.user_id = u.id " +
                            "LEFT JOIN follow f ON u.id = f.following_userId " +
                            "LEFT JOIN profile_pic_table t2 ON u.id = t2.user_id " +
                            "LEFT JOIN userActivation_table uAt ON u.id = uAt.user_id " +
                            "where f.userId=:id AND f.isFollowing = true ORDER BY t.created_date DESC";

            Query query = entityManager.createNativeQuery(SQL_QUERY).setParameter("id", user.getId());
            int totalItems = query.getResultList().size();
            query.setFirstResult((pageable.getPageNumber() - 1) * pageable.getPageSize());
            query.setMaxResults(pageable.getPageSize());

            List<Object[]> allList = query.getResultList();
            List<UserPostDto> userPostDtoList = new ArrayList<UserPostDto>();

            for (Object[] o : allList) {
                LikeActiondto likeActiondto = likesService.getLikesCountForImage(o[2].toString(), user);
                UserPostDto userPostDto = PhotoUtils.convertObjectToUserPhotos(o, likeActiondto, totalItems);
                userPostDtoList.add(userPostDto);
            }
            return userPostDtoList;
        }
    }

    public List<UserPhotodto> getAllPhotos(String username) {
        List<UserPhotos> photoList = photoRepository.getUserPhotosByUserUsername(username);
        List<UserPhotodto> userPhotodto = PhotoUtils.convertUserPhotos(photoList);
        return userPhotodto;
    }

    public UserPhotos getPhotos(String image_path) {
        UserPhotos userPhotos = photoRepository.getUserPhotosByImage_path(image_path);
        return userPhotos;
    }

    public long getPhotoCount(String username) {
        List<UserPhotos> userPhotosList = photoRepository.getUserPhotosByUserUsername(username);
        return userPhotosList.size();
    }

    public void deletePhoto(UserPhotos userPhotos) {
        photoRepository.delete(photoRepository.getUserPhotosByImage_path(userPhotos.getImage_path()));
    }

    public void updateCaption(UserPhotodto userPhotodto) {
        User user = userService.getUser(userPhotodto.getUsername());
        UserPhotos userPhotos1 = photoRepository.getUserPhotosByImage_path(userPhotodto.getImage_path());
        userPhotos1.setImage_path(userPhotodto.getImage_path());
        userPhotos1.setUser(user);
        userPhotos1.setCaption(userPhotodto.getCaption());
        photoRepository.save(userPhotos1);
    }

    @Override
    public String getCaption(String imageName) {
        UserPhotos userPhotos = photoRepository.getUserPhotosByImage_path(imageName);
        return userPhotos.getCaption();
    }
}

