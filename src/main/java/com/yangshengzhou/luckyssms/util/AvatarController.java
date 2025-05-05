//package com.yangshengzhou.luckyssms.util;
//import com.yangshengzhou.luckyssms.mapper.student.StudentMapper;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.GetMapping;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.web.bind.annotation.PathVariable;
//import org.springframework.web.bind.annotation.RestController;
//
//import java.util.Base64;
//
//@RestController
//public class AvatarController {
//
//    @Autowired
//    private StudentMapper studentMapper;
//
//    @GetMapping("/api/avatar/{uid}")
//    public ResponseEntity<String> getAvatar(@PathVariable("uid") int uid) {
//        byte[] avatarData = studentMapper.getAvatarByUid(uid);
//        if (avatarData != null && avatarData.length > 0) {
//            String base64Image = Base64.getEncoder().encodeToString(avatarData);
//            return ResponseEntity.ok()
//                    .body(base64Image);
//        } else {
//            return ResponseEntity.notFound().build();
//        }
//    }
//}