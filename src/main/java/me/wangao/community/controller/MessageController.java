package me.wangao.community.controller;

import me.wangao.community.entity.Message;
import me.wangao.community.entity.Page;
import me.wangao.community.entity.User;
import me.wangao.community.service.MessageService;
import me.wangao.community.service.UserService;
import me.wangao.community.util.CommunityUtil;
import me.wangao.community.util.HostHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.*;

@Controller
public class MessageController {

    @Resource
    private MessageService messageService;

    @Resource
    private HostHolder hostHolder;

    @Resource
    private UserService userService;

    // 私信列表
    @GetMapping("/letter/list")
    public String getLetterList(Model model, Page page) {
        User user = hostHolder.getUser();

        // 分页
        page.setLimit(5);
        page.setPath("/letter/list");
        page.setRows(messageService.findConversationCount(user.getId()));

        // 会话列表
        List<Message> conversationList = messageService.findConversations(user.getId(), page.getOffset(), page.getLimit());
        List<Map<String, Object>> conversations = new ArrayList<>();

        if (conversationList != null) {
            for (Message message : conversationList) {
                Map<String, Object> map = new HashMap<>();
                map.put("conversation", message);
                map.put("unreadCount", messageService.findLetterUnreadCount(user.getId(), message.getConversationId()));
                map.put("letterCount", messageService.findLetterCount(message.getConversationId()));
                int targetId;
                if (Objects.equals(user.getId(), message.getFromId())) {
                    targetId = message.getToId();
                } else {
                    targetId = message.getFromId();
                }
                map.put("target", userService.findUserById(targetId));

                conversations.add(map);
            }
        }

        model.addAttribute("conversations", conversations);

        // 查询未读消息数量
        int letterUnreadCount = messageService.findLetterUnreadCount(user.getId(), null);
        model.addAttribute("letterUnreadCount", letterUnreadCount);

        return "/site/letter";
    }

    @GetMapping("letter/detail/{conversationId}")
    public String getLetterDetail(@PathVariable String conversationId, Page page, Model model) {
        // 分页
        page.setLimit(5);
        page.setPath("/letter/detail/" + conversationId);
        page.setRows(messageService.findLetterCount(conversationId));

        // 私信列表
        List<Message> letterList = messageService.findLetters(conversationId, page.getOffset(), page.getLimit());
        List<Map<String, Object>> letters = new ArrayList<>();
        if (letterList != null) {
            for (Message message : letterList) {
                Map<String, Object> map = new HashMap<>();
                map.put("letter", message);
                map.put("fromUser", userService.findUserById(message.getFromId()));
                letters.add(map);
            }
        }
        model.addAttribute("letters", letters);

        // 私信目标用户，防止解析错误用当前用户占位
        User target = hostHolder.getUser();
        if (letterList != null) {
            Message message = letterList.get(0);
            User user = hostHolder.getUser();
            if (Objects.equals(user.getId(), message.getFromId())) {
                target = userService.findUserById(message.getToId());
            } else {
                target = userService.findUserById(message.getFromId());
            }
        }
        model.addAttribute("target", target);

        // 将未读消息改为已读
        List<Integer> unreadLetterIds = getUnreadLetterIds(letterList);
        if (!unreadLetterIds.isEmpty()) {
            messageService.readMessage(unreadLetterIds);
        }

        return "/site/letter-detail";
    }

    private List<Integer> getUnreadLetterIds(List<Message> letterList) {
        List<Integer> ids = new ArrayList<>();
        User user = hostHolder.getUser();

        if (letterList != null) {
            letterList.forEach(letter -> {
                if (Objects.equals(letter.getToId(), user.getId()) && letter.getStatus() == 0) {
                    ids.add(letter.getId());
                }
            });
        }

        return ids;
    }

    @PostMapping("/letter/send")
    @ResponseBody
    public String sendLetter(String toName, String content) {
        User target = userService.findUserByName(toName);
        if (target == null) {
            return CommunityUtil.getJSONString(1, "用户不存在");
        }

        Message message = new Message()
                .setFromId(hostHolder.getUser().getId())
                .setToId(target.getId())
                .setContent(content);

        messageService.addMessage(message);

        return CommunityUtil.getJSONString(0, "发送成功");
    }
}
