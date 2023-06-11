package com.grinder.audio;

import com.grinder.client.GameShell;
import com.runescape.audio.Audio;
import com.runescape.audio.SoundEffect;
import com.runescape.cache.OsCache;

import javax.swing.*;
import java.awt.*;

public final class SoundEffectPlayer {

    public static void main(String[] args) {

        OsCache.loadAndWait();

        GameShell.taskHandler.newThreadTask(() -> {
            while(true){
                Audio.processSoundEffects();
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }, 1);


        final DefaultListModel<SoundElement> listModel = new DefaultListModel<>();
        final int soundCount = OsCache.indexCache4.getArchiveCount();
        for(int soundId = 0; soundId < soundCount; soundId++){
            final SoundEffect effect = SoundEffect.get(OsCache.indexCache4, soundId, 0);
            listModel.addElement(new SoundElement(soundId, effect));
        }

        final JFrame frame = new JFrame("Sound Viewer");
        frame.setLayout(new BorderLayout());

        final JScrollPane scrollPane = new JScrollPane();
        final JList<SoundElement> soundElementJList = new JList<>(listModel);
        scrollPane.setViewportView(soundElementJList);

        frame.add(scrollPane, BorderLayout.CENTER);

        final Box box = new Box(BoxLayout.X_AXIS);
        final JButton playButton = new JButton("Queue sound");
        final JTextField unknownField = new JTextField();
        final JTextField delayField = new JTextField();
        playButton.addActionListener(e -> {
            final SoundElement soundElement = soundElementJList.getSelectedValue();
            final int unknownValue = unknownField.getText().isEmpty() ? 1 : Integer.parseInt(unknownField.getText());
            final int delay = delayField.getText().isEmpty() ? 0 : Integer.parseInt(delayField.getText());
            Audio.queueSoundEffect(soundElement.id, unknownValue, delay);
        });
        box.add(playButton);
        box.add(unknownField);
        box.add(delayField);
        frame.add(box, BorderLayout.SOUTH);

        frame.pack();
        frame.setVisible(true);
    }

    static class SoundElement {

        private final int id;
        private final SoundEffect effect;

        public SoundElement(int id, SoundEffect effect) {
            this.id = id;
            this.effect = effect;
        }

        @Override
        public String toString() {
            return "SoundElement{" +
                    "id=" + id +
                    ", effect=" + effect +
                    '}';
        }
    }
}