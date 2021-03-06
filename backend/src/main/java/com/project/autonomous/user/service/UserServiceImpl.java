package com.project.autonomous.user.service;

import com.project.autonomous.common.exception.CustomException;
import com.project.autonomous.common.exception.ErrorCode;
import com.project.autonomous.jwt.util.SecurityUtil;
import com.project.autonomous.picture.entity.Picture;
import com.project.autonomous.picture.repository.PictureRepository;
import com.project.autonomous.picture.service.DBFileStorageService;
import com.project.autonomous.team.entity.SportCategory;
import com.project.autonomous.team.entity.Team;
import com.project.autonomous.team.repository.SportCategoryRepository;
import com.project.autonomous.user.dto.request.CheckPasswordReq;
import com.project.autonomous.user.dto.request.InterestReq;
import com.project.autonomous.user.dto.request.UserModifyReq;
import com.project.autonomous.user.dto.response.MyInfoRes;
import com.project.autonomous.user.dto.response.UserInfoRes;
import com.project.autonomous.user.dto.response.UserInterestRes;
import com.project.autonomous.user.dto.response.UserTeamListRes;
import com.project.autonomous.user.entity.User;
import com.project.autonomous.user.entity.UserInterest;
import com.project.autonomous.user.entity.UserInterestId;
import com.project.autonomous.user.entity.UserTeam;
import com.project.autonomous.user.repository.UserInterestRepository;
import com.project.autonomous.user.repository.UserRepository;
import com.project.autonomous.user.repository.UserTeamRepository;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserTeamRepository userTeamRepository;
    private final PictureRepository pictureRepository;
    private final SportCategoryRepository sportCategoryRepository;
    private final UserInterestRepository userInterestRepository;
    private final PasswordEncoder passwordEncoder;
    private final DBFileStorageService dbFileStorageService;

    // ???????????? ??????
    public boolean checkPassword(CheckPasswordReq checkPasswordReq) {
        String currentPass = userRepository.findById(SecurityUtil.getCurrentMemberId())
            .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND)).getPassword();

        return passwordEncoder.matches(checkPasswordReq.getPassword(), currentPass);
    }

    // ???????????? ??????
    @Transactional
    public void changePassword(CheckPasswordReq checkPasswordReq) {
        User user = userRepository.findById(SecurityUtil.getCurrentMemberId())
            .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        user.changePassword(checkPasswordReq.encodePassword(passwordEncoder));
    }

    // ?????? ??????
    @Transactional
    public MyInfoRes modifyUser(UserModifyReq userModifyReq) throws IOException {
        User user = findMember(SecurityUtil.getCurrentMemberId());

        Picture picture;
        if(user.getPicture() != null) {
            dbFileStorageService.deleteFile(user.getPicture().getId());
            pictureRepository.delete(user.getPicture());
        }
        if (userModifyReq.getFile() == null) {
            picture = null;
        } else {
            picture = dbFileStorageService.storeFile(userModifyReq.getFile());
        }

        user.update(userModifyReq, picture);
        return MyInfoRes.from(user);
    }

    // ?????? ??? ?????? (?????? ?????????)
    public Slice<UserTeamListRes> getMyTeams(Pageable pageable) {
        Slice<Team> teams = userTeamRepository.findTeamByUser(
            findMember(SecurityUtil.getCurrentMemberId()), pageable);
        return teams.map(p -> UserTeamListRes.from(p));
    }

    // ?????? ?????? ?????? ??????
    public MyInfoRes getMyInfo() {
        return MyInfoRes.from(findMember(SecurityUtil.getCurrentMemberId()));
    }

    // ?????? ?????? ?????? ??????
    public List<UserInterestRes> getMyInterest() {
        List<UserInterest> interests = userInterestRepository.findAllByUserInterestIdUser(
            findMember(SecurityUtil.getCurrentMemberId()));
        return interests.stream()
            .map(UserInterestRes::from)
            .collect(Collectors.toList());
    }

    // ?????? ?????? ?????? ????????????
    @Transactional
    public List<UserInterestRes> updateInterest(InterestReq interestReq) {
        User user = findMember(SecurityUtil.getCurrentMemberId());
        userInterestRepository.deleteAllByUserInterestIdUser(user);

        if (interestReq.getInterests() == null) {
            return getMyInterest();
        }

        for (String interest : interestReq.getInterests()) {
            SportCategory sportCategory = findSportCategory(interest);
            UserInterestId id = new UserInterestId(user, sportCategory);

            userInterestRepository.save(new UserInterest(id));
        }
        return getMyInterest();
    }

    @Transactional
    public void deleteUser() {
        User user = findMember(SecurityUtil.getCurrentMemberId());

        // ???????????? ????????? ??????, ????????? ?????? ??????
        List<UserTeam> userTeams = userTeamRepository.findAllByUser(user);
        for (UserTeam userTeam : userTeams) {
            if (userTeam.getAuthority().equals("??????")) {
                throw new CustomException(ErrorCode.STILL_YOU_HAVE_SREINEDS);
            }
            userTeamRepository.delete(userTeam);
        }
        // ????????? ????????? ?????????, ?????? ?????? ??????

        userRepository.delete(user);
    }

    // ?????? ?????? ??????
    public UserInfoRes getUserInfo(Long userId) {
        return UserInfoRes.from(findMember(userId));
    }

    public User findMember(Long userId) {
        return userRepository.findById(userId)
            .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
    }

    public SportCategory findSportCategory(String name) {
        return sportCategoryRepository.findByName(name)
            .orElseThrow(() -> new CustomException(ErrorCode.SPORT_CATEGORY_NOT_FOUND));
    }
}