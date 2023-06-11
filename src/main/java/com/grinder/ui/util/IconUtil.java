/*
 * Copyright 2000-2013 JetBrains s.r.o.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.grinder.ui.util;

import javax.swing.*;
import java.awt.*;

/**
 * TODO: add documentation
 *
 * @author Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @version 1.0
 * @since 08/12/2019
 */
public class IconUtil {

    public static void paintInCenterOf(Component c, Graphics g, Icon icon) {
        final int x = (c.getWidth() - icon.getIconWidth()) / 2;
        final int y = (c.getHeight() - icon.getIconHeight()) / 2;
        icon.paintIcon(c, g, x, y);
    }

}
