package com.board.service;

import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import com.board.domain.CharacterDTO;
import com.board.domain.MailDTO;
import com.board.domain.SurveyOutputDTO;
import com.board.domain.UserDTO;
import com.board.mapper.UserMapper;

@Service
public class UserServiceImpl implements UserService {

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	public UserMapper userMapper;

	@Override
	public UserDTO loginCheck(String id, String pw) {
		UserDTO loginMember = userMapper.selectUserDetail(id);
		if (loginMember == null) {
			return null;
		} else if (!passwordEncoder.matches(pw, loginMember.getPw())) {
			return null;
		} else {
			System.out.println(pw);
			System.out.println(loginMember.getPw());
			loginMember.setUserCharacter(userMapper.selectUserCharacter(id));
			return loginMember;
		}
	}

	@Override
	public UserDTO getUserDetail(String ID) {
		UserDTO user = userMapper.selectUserDetail(ID);
		if(user!=null) {
			user.setUserCharacter(userMapper.selectUserCharacter(ID));
		}
		return user;
	}

	@Override
	public List<SurveyOutputDTO> getUserHistory(String ID) {
		return userMapper.selectUserHistory(ID);
	}

	@Override
	public int updateUserDetail(UserDTO params) {
		return userMapper.updateUser(params);
	}

	@Override
	public UserDTO findLoginId(String Email) {
		UserDTO findUserEmail = userMapper.findId(Email);
		if (findUserEmail == null) {
			return null;
		} else {
			return findUserEmail;
		}
	}

	@Override
	public UserDTO findLoginPw(String Id, String PwHint) {
		UserDTO selectId = userMapper.selectUserDetail(Id);
		if (selectId == null) {
			return null;
		} else if (!PwHint.equals(selectId.getPwHint())) {
			return null;
		} else {
			return selectId;
		}
	}

	@Override
	public List<UserDTO> getUserList() {
		List<UserDTO> userList = Collections.emptyList();

		int userCount = userMapper.selectUserTotalCount();
		if(userCount > 0) {
			userList = userMapper.selectUserList();
			for(int i=0; i<userList.size(); i++) {
				UserDTO user = userList.get(i);
				user.setUserCharacter(userMapper.selectUserCharacter(user.getId()));
			}
		}
		return userList;
	};

	// ?????? ?????? ??????
	@Override
	public MailDTO createMailContent(String Email) {
		String str = getTempPassword();
        MailDTO dto = new MailDTO();
        dto.setAddress(Email);
        dto.setStr(str);
        dto.setTitle("VerDeTerr ?????????????????? ?????? ????????? ?????????.");
        dto.setMessage("???????????????. VerDeTerr ?????????????????? ?????? ?????? ????????? ?????????." + " ???????????? ?????? ??????????????? "
                + str + " ?????????." + "????????? ?????? ??????????????? ??????????????????");
       // updatePassword(str,Email);
        return dto;
	}
	
	// ?????????????????? ??????
    @Override
    public String getTempPassword(){
        char[] charSet = new char[] { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F',
                'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z' };

        String str = "";

        // ?????? ?????? ????????? ?????? ???????????? 10?????? ?????? ????????? ?????????
        int idx = 0;
        for (int i = 0; i < 10; i++) {
            idx = (int) (charSet.length * Math.random());
            str += charSet[idx];
        }
        return str;
    }
    
    // ????????????????????? ????????????
    @Override
    public void newPassword(String str, String id) {
    	UserDTO user = userMapper.selectUserDetail(id);
    	String encodedPW = passwordEncoder.encode(str);
    	user.setPw(encodedPW);
    	System.out.println("?????? ???????????? : " + str);
    	System.out.println("???????????? ???????????? : " + encodedPW);
    	userMapper.updateUser(user);
    }
    
    @Autowired
    private JavaMailSender javaMailSender;
    
    // ?????? ?????????
    @Override
    public void mailSend(MailDTO mailDTO) {
        System.out.println("?????? ??????!");
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(mailDTO.getAddress());
        message.setSubject(mailDTO.getTitle());
        message.setText(mailDTO.getMessage());
        message.setFrom("verdeterr123@naver.com");
        message.setReplyTo("verdeterr123@naver.com");
        System.out.println("message"+message);
        javaMailSender.send(message);
    }
    
    

}