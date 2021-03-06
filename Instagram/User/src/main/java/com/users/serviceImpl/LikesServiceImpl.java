package com.users.serviceImpl;

import com.users.dto.Commentsdto;
import com.users.dto.LikeActiondto;
import com.users.dto.Likesdto;
import com.users.model.Likes;
import com.users.model.User;
import com.users.model.UserPhotos;
import com.users.repository.LikesRepository;
import com.users.repository.PhotoRepository;
import com.users.repository.UserRepository;
import com.users.service.LikesService;
import com.users.utils.LikesUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class LikesServiceImpl implements LikesService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PhotoRepository photoRepository;
    @Autowired
    private LikesRepository likesRepository;

    public LikeActiondto saveLike(Commentsdto commentsdto) {
        User user = userRepository.getUserByUsername(commentsdto.getUsername());
        UserPhotos userPhotos = photoRepository.getUserPhotosByImage_path(commentsdto.getImage_path());
        Likes liked = likesRepository.getByUserIdAndPhotoId(user.getId(), userPhotos.getId());
        LikeActiondto likeActiondto = new LikeActiondto();
        if (liked != null) {
            if (liked.isLiked()) {
                liked.setLiked(false);
                likesRepository.delete(liked);
            }
            likeActiondto.setShowRedButton(false);
            likeActiondto.setLikeCount(getCountOfLikes(liked));
            return likeActiondto;
        }
        Likes likes = LikesUtil.generateLikes(user, userPhotos);
        likesRepository.save(likes);
        likeActiondto.setShowRedButton(true);
        likeActiondto.setLikeCount(getCountOfLikes(likes));
        return likeActiondto;
    }

    public int getCountOfLikes(Likes likes) {
        int countofLikes = 0;
        List<Likesdto> list = getLikesList(likes.getUserPhotos().getImage_path());
        for (Likesdto like : list) {
            if (like.getActivationStatus().equals("activated")) {
                countofLikes += 1;
            }
        }
        return countofLikes;
    }

    public List<Likes> getByPhotoId(long id) {
        return likesRepository.getByUserPhotos_Id(id);
    }

    public LikeActiondto getLikesCountForImage(String imageName, User user) {
        List<Likes> likesList = likesRepository.getByUserPhotos_Image_path(imageName);
        LikeActiondto likeActiondto = new LikeActiondto();
        likeActiondto.setShowRedButton(false);
        int c =0;
        if (likesList == null) {
            likeActiondto.setShowRedButton(false);
        } else {
            for (Likes likes : likesList) {
                if (likes.getUser().getUsername().equals(user.getUsername())) {
                   c= c+1;
                }
            }
            if (c>0){
                likeActiondto.setShowRedButton(true);
            }
            else {
                likeActiondto.setShowRedButton(false);
            }
        }
        List<Likesdto> list = LikesUtil.convertLikesToLikesDto(likesList);
        List<Likesdto> likesResult = new ArrayList<>();
        for (Likesdto likesdto : list) {
            if (likesdto.getActivationStatus().equals("activated")) {
                likesResult.add(likesdto);
            }
        }
        likeActiondto.setLikeCount(likesResult.size());
        return likeActiondto;
    }

    public List<Likesdto> getLikesList(String imageName) {
        List<Likes> likesList = likesRepository.getByUserPhotos_Image_path(imageName);
        List<Likesdto> list = LikesUtil.convertLikesToLikesDto(likesList);
        List<Likesdto> resultList = new ArrayList<Likesdto>();
        for (Likesdto like : list) {
            if (like.getActivationStatus().equals("activated")) {
                resultList.add(like);
            }
        }
        return resultList;
    }

}
