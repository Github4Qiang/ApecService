package edu.scu.qz.service;


import edu.scu.qz.common.ServerResponse;
import edu.scu.qz.dao.pojo.User;

public interface IUserService {

    ServerResponse<User> login(String username, String password);

    ServerResponse<String> register(User user);

    ServerResponse<String> checkValid(String str, String type);

    ServerResponse<String> selectQuestion(String username);

    ServerResponse<String> checkAnswer(String username, String question, String answer);

    ServerResponse<String> forgetResetPassword(String username, String passwordNew, String forgetToken);

    ServerResponse<String> resetPassword(String passwordOld, String passwordNew, User user);

    ServerResponse<User> updateInformation(User user);

    ServerResponse<User> getInformation(Integer userId);

    ServerResponse checkAdminRole(User user);

    ServerResponse checkApplicantRole(User user);

    ServerResponse checkCandidateRole(User user);

    ServerResponse checkProducerRole(User user);

    ServerResponse changeRole(Integer id, int roleApplicant);

    ServerResponse getUserList(Integer pageNum, Integer pageSize, Integer role);
}
