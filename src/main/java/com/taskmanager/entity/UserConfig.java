package com.taskmanager.entity;

import com.taskmanager.entity.enums.Theme;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserConfig implements Serializable {

    private Theme defaultTheme = Theme.LIGHT;
}
