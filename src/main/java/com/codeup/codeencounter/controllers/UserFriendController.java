package com.codeup.codeencounter.controllers;

import com.codeup.codeencounter.models.Gallery;
import com.codeup.codeencounter.models.Status;
import com.codeup.codeencounter.models.User;
import com.codeup.codeencounter.models.UserFriend;
import com.codeup.codeencounter.repositories.*;
import com.codeup.codeencounter.services.EmailService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Controller
public class UserFriendController {

    public UserRepo userRepo;
    public UserFriendRepo userFriendRepo;
    public PostRepo postRepo;
    public CommentRepo commentRepo;
    public PictureRepo pictureRepo;
    public GalleryRepo galleryRepo;
    private final EmailService emailService;

    public UserFriendController(UserRepo userRepo, UserFriendRepo userFriendRepo,
                                PostRepo postRepo, CommentRepo commentRepo,
                                PictureRepo pictureRepo, GalleryRepo galleryRepo, EmailService emailService) {
        this.userRepo = userRepo;
        this.userFriendRepo = userFriendRepo;
        this.postRepo = postRepo;
        this.commentRepo = commentRepo;
        this.galleryRepo = galleryRepo;
        this.pictureRepo = pictureRepo;
        this.emailService = emailService;
    }

    //Create UserFriend (Friend request)
    @PostMapping("/request/{username}/{friendName}")
    public String addFriend(@PathVariable String username,
                            @PathVariable String friendName) {
        if (userFriendRepo.findByUserAndFriend(userRepo.findByUsername(username), userRepo.findByUsername(friendName)) != null) {
            return "redirect:/";
        }
        UserFriend userFriend = new UserFriend();
        userFriend.setFriend(userRepo.findByUsername(friendName));
        userFriend.setUser(userRepo.findByUsername(username));
        userFriend.setStatus(Status.PENDING);
        userFriendRepo.save(userFriend);

//        emailService.prepareAndSend(userRepo.findByUsername(friendName), friendName + ", someone wants to be your friend :)",
//                "Looks like you're popular! You might have a friend in " + username + " . Head to the friend request page on your" +
//                        " justfriends.online profile to let them know if you'd like to be friends!");

        return "redirect:/";
    }

    //Delete UserFriend (Unfriend)
    @PostMapping("/{username}/{friendName}/delete")
    public String deleteFriend(@PathVariable String username,
                               @PathVariable String friendName) {
        User user = userRepo.findByUsername(username);
        User friend = userRepo.findByUsername(friendName);
        UserFriend userUserFriend = userFriendRepo.findByUserAndFriend(user, friend);
        UserFriend friendUserFriend = userFriendRepo.findByUserAndFriend(friend, user);

        userFriendRepo.delete(userUserFriend);
        userFriendRepo.delete(friendUserFriend);

        return "redirect:/user/" + username;
    }

    //Read Friend requests
    @GetMapping("/{username}/friends/requests")
    public String showFriendRequests(@PathVariable String username,
                                     Model model) {
        User user = userRepo.findByUsername(username);
        List<UserFriend> userFriendRequests = userFriendRepo.findAllByFriendAndStatus(user, Status.PENDING);//requests someone else sent

        model.addAttribute("friendRequests", userFriendRequests);
        model.addAttribute("user", user);

        return "userFriend/friend-requests";
    }

    //Reject Friend request
    @PostMapping("/request/{username}/{friendName}/reject")
    public String rejectFriend(@PathVariable String username,
                               @PathVariable String friendName) {
        User user = userRepo.findByUsername(username);
        User friend = userRepo.findByUsername(friendName);

        UserFriend updatedUserFriend = userFriendRepo.findByUserAndFriend(friend, user);
        userFriendRepo.delete(updatedUserFriend);

        return "redirect:/user/" + username;
    }

// todo create blocked users page & mechanism

    //Accept Friend request
    @PostMapping("/request/{username}/{friendName}/accept")
    public String acceptFriend(@PathVariable String username,
                               @PathVariable String friendName) {
        User user = userRepo.findByUsername(username);
        User friend = userRepo.findByUsername(friendName);

        UserFriend updatedUserFriend = userFriendRepo.findByUserAndFriend(friend, user);
        updatedUserFriend.setStatus(Status.ACCEPTED);
        userFriendRepo.save(updatedUserFriend);

        return "redirect:/user/" + username;
    }

    //View Friend profile
    @GetMapping("/{username}/friend/{friendName}")
    public String showFriendProfile(@PathVariable String username,
                                    @PathVariable String friendName,
                                    Model model) {
        User currentUser = userRepo.findByUsername(username);
        User sessionUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User friend = userRepo.findByUsername(friendName);
        List<UserFriend> friendUserFriends1 = userFriendRepo.findAllByUserAndStatus(friend, Status.ACCEPTED);
        List<UserFriend> friendUserFriends2 = userFriendRepo.findAllByFriendAndStatus(friend, Status.ACCEPTED);
        ArrayList<User> friendFriends = new ArrayList<>();// lists User objects of friend's userFriends
        for (UserFriend userFriend : friendUserFriends1) {
            friendFriends.add(userFriend.getFriend());
        }
        for (UserFriend userFriend : friendUserFriends2) {
            friendFriends.add(userFriend.getUser());
        }

        List<UserFriend> userUserFriends1 = userFriendRepo.findAllByUserAndStatus(currentUser, Status.ACCEPTED);// user's friend list
        List<UserFriend> userUserFriends2 = userFriendRepo.findAllByFriendAndStatus(currentUser, Status.ACCEPTED);
        ArrayList<User> userFriends = new ArrayList<>();
        for (UserFriend userFriend : userUserFriends1) {
            userFriends.add(userFriend.getFriend());
        }
        for (UserFriend userFriend : userUserFriends2) {
            userFriends.add(userFriend.getUser());
        }
        List<Gallery> friendGalleries = galleryRepo.findAllByUser(friend);

        List<UserFriend> pendingUserFriends1 = userFriendRepo.findAllByUserAndStatus(currentUser, Status.PENDING);// user pending friends
        List<UserFriend> pendingUserFriends2 = userFriendRepo.findAllByFriendAndStatus(currentUser, Status.PENDING);
        ArrayList<User> pendingFriends = new ArrayList<>();
        for (UserFriend userFriend : pendingUserFriends1) {
            pendingFriends.add(userFriend.getFriend());
        }
        for (UserFriend userFriend : pendingUserFriends2) {
            pendingFriends.add(userFriend.getUser());
        }

        model.addAttribute("pendingFriends", pendingFriends);
        model.addAttribute("friendFriends", friendFriends);
        model.addAttribute("friend", friend);
        model.addAttribute("userFriendList", userFriends);
        model.addAttribute("currentUser", currentUser);
        if (!friendGalleries.isEmpty()) {
            model.addAttribute("galleries", friendGalleries);
        }
        model.addAttribute("sessionUser", sessionUser);

        return "userFriend/friend-profile";
    }

    //Discover friends page
    @GetMapping("/users/search/{username}")
    public String viewAllAdsWithAjax(@PathVariable String username,
                                     Model model) {
        User currentUser = userRepo.findByUsername(username);
        List<UserFriend> userUserFriends1 = userFriendRepo.findAllByUserAndStatus(currentUser, Status.ACCEPTED);// user's friend list
        List<UserFriend> userUserFriends2 = userFriendRepo.findAllByFriendAndStatus(currentUser, Status.ACCEPTED);
        ArrayList<User> userFriends = new ArrayList<>();
        for (UserFriend userFriend : userUserFriends1) {
            userFriends.add(userFriend.getFriend());
        }
        for (UserFriend userFriend : userUserFriends2) {
            userFriends.add(userFriend.getUser());
        }
        List<UserFriend> pendingUserFriends1 = userFriendRepo.findAllByUserAndStatus(currentUser, Status.PENDING);// user pending friends
        List<UserFriend> pendingUserFriends2 = userFriendRepo.findAllByFriendAndStatus(currentUser, Status.PENDING);
        ArrayList<User> pendingFriends = new ArrayList<>();
        for (UserFriend userFriend : pendingUserFriends1) {
            pendingFriends.add(userFriend.getFriend());
        }
        for (UserFriend userFriend : pendingUserFriends2) {
            pendingFriends.add(userFriend.getUser());
        }

        model.addAttribute("pendingFriends", pendingFriends);
        model.addAttribute("allUsers", userRepo.findAll());
        model.addAttribute("user", currentUser);
        model.addAttribute("userFriends", userFriends);
        return "user/search";
    }
    @PostMapping("/users/search/{username}")
    public String joinCohort(@RequestParam(name = "search") String search,
                             Model model,
                             @PathVariable String username) {
        User currentUser = userRepo.findByUsername(username);
        List<UserFriend> userUserFriends1 = userFriendRepo.findAllByUserAndStatus(currentUser, Status.ACCEPTED);// user's friend list
        List<UserFriend> userUserFriends2 = userFriendRepo.findAllByFriendAndStatus(currentUser, Status.ACCEPTED);
        List<User> userFriends = new ArrayList<>();
        for (UserFriend userFriend : userUserFriends1) {
            userFriends.add(userFriend.getFriend());
        }
        for (UserFriend userFriend : userUserFriends2) {
            userFriends.add(userFriend.getUser());
        }

        List<User> filteredList = new ArrayList<>();
        for (User user : userRepo.findAll()) {
            if (user.getUsername().toLowerCase().contains(search.toLowerCase())
                    || user.getFirstName().toLowerCase().contains(search.toLowerCase())
                    || user.getLastName().toLowerCase().contains(search.toLowerCase())) {
                filteredList.add(user);
            }
        }

        List<UserFriend> pendingUserFriends1 = userFriendRepo.findAllByUserAndStatus(currentUser, Status.PENDING);// pending friends
        List<UserFriend> pendingUserFriends2 = userFriendRepo.findAllByFriendAndStatus(currentUser, Status.PENDING);
        ArrayList<User> pendingFriends = new ArrayList<>();
        for (UserFriend userFriend : pendingUserFriends1) {
            pendingFriends.add(userFriend.getFriend());
        }
        for (UserFriend userFriend : pendingUserFriends2) {
            pendingFriends.add(userFriend.getUser());
        }

        model.addAttribute("pendingFriends", pendingFriends);
        model.addAttribute("allUsers", filteredList);
        model.addAttribute("user", currentUser);
        model.addAttribute("userFriends", userFriends);
        model.addAttribute("search", search);
        return "user/search";
    }
}