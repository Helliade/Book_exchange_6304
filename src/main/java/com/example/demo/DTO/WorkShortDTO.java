package com.example.demo.DTO;

import com.example.demo.Models.Work;

import java.util.HashSet;
import java.util.Set;

public class WorkShortDTO {
    private Long id;
    private String name;
    private String writer;

    // Геттеры и сеттеры
    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getWriter() {
        return writer;
    }

    public WorkShortDTO(Work work) {
        this.id = work.getId();
        this.name = work.getName();
        this.writer = work.getWriter();
    }

    public static Set<WorkShortDTO> convertWorksToShortDTO(Set<Work> works) {
        Set<WorkShortDTO> result = new HashSet<>();
        for (Work work : works) {
            result.add(new WorkShortDTO(work));
        }
        return result;
    }

    @Override
    public String toString() {
        return "WorkShortDTO{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", writer='" + writer +
                '}';
    }
}
