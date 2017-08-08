package com.courseheap.repositories;

/**
 * Created by ashish.p on 8/8/17.
 */

import com.courseheap.entities.DiscussionForum;
import com.courseheap.entities.PlayList;
import com.courseheap.entities.UserRegistration;
import org.springframework.data.repository.CrudRepository;

import java.util.List;


public interface DiscussionForumRepository extends CrudRepository<DiscussionForum, Long> {

    DiscussionForum findById(Long id);
    List<DiscussionForum> findByCourse_Id(long courseId);
    List<DiscussionForum> findByPlaylist_Id(long playlistId);
}
