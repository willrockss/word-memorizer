package it.sauronsoftware.jave;

import java.io.File;
import java.io.IOException;

//TODO Вынести все эти впомогательные классы в собственную библиотечку в виде модуля
public class CustomEncoder extends Encoder {

    /**
     * The locator of the ffmpeg executable used by this encoder.
     */
    private FFMPEGLocator locator;

    public CustomEncoder() {
        locator = new DefaultFFMPEGLocator();
    }

    @Override
    public void encode(File source, File target, EncodingAttributes attributes,
                       EncoderProgressListener listener) throws IllegalArgumentException,
            InputFormatException, EncoderException {
        String formatAttribute = attributes.getFormat();
        Float offsetAttribute = attributes.getOffset();
        Float durationAttribute = attributes.getDuration();
        AudioAttributes audioAttributes = attributes.getAudioAttributes();
        VideoAttributes videoAttributes = attributes.getVideoAttributes();
        if (audioAttributes == null && videoAttributes == null) {
            throw new IllegalArgumentException(
                    "Both audio and video attributes are null");
        }
        target = target.getAbsoluteFile();
        target.getParentFile().mkdirs();
        FFMPEGExecutor ffmpeg = locator.createExecutor();
        if (offsetAttribute != null) {
            ffmpeg.addArgument("-ss");
            ffmpeg.addArgument(String.valueOf(offsetAttribute.floatValue()));
        }
        ffmpeg.addArgument("-i");
        ffmpeg.addArgument(source.getAbsolutePath());
        if (durationAttribute != null) {
            ffmpeg.addArgument("-t");
            ffmpeg.addArgument(String.valueOf(durationAttribute.floatValue()));
        }
        if (videoAttributes == null) {
            ffmpeg.addArgument("-vn");
        } else {
            String codec = videoAttributes.getCodec();
            if (codec != null) {
                ffmpeg.addArgument("-vcodec");
                ffmpeg.addArgument(codec);
            }
            String tag = videoAttributes.getTag();
            if (tag != null) {
                ffmpeg.addArgument("-vtag");
                ffmpeg.addArgument(tag);
            }
            Integer bitRate = videoAttributes.getBitRate();
            if (bitRate != null) {
                ffmpeg.addArgument("-b");
                ffmpeg.addArgument(String.valueOf(bitRate.intValue()));
            }
            Integer frameRate = videoAttributes.getFrameRate();
            if (frameRate != null) {
                ffmpeg.addArgument("-r");
                ffmpeg.addArgument(String.valueOf(frameRate.intValue()));
            }
            VideoSize size = videoAttributes.getSize();
            if (size != null) {
                ffmpeg.addArgument("-s");
                ffmpeg.addArgument(String.valueOf(size.getWidth()) + "x"
                        + String.valueOf(size.getHeight()));
            }
        }
        if (audioAttributes == null) {
            ffmpeg.addArgument("-an");
        } else {
            String codec = audioAttributes.getCodec();
            if (codec != null) {
                ffmpeg.addArgument("-acodec");
                ffmpeg.addArgument(codec);
            }
            Integer bitRate = audioAttributes.getBitRate();
            if (bitRate != null) {
                ffmpeg.addArgument("-ab");
                ffmpeg.addArgument(String.valueOf(bitRate.intValue()));
            }
            Integer channels = audioAttributes.getChannels();
            if (channels != null) {
                ffmpeg.addArgument("-ac");
                ffmpeg.addArgument(String.valueOf(channels.intValue()));
            }
            Integer samplingRate = audioAttributes.getSamplingRate();
            if (samplingRate != null) {
                ffmpeg.addArgument("-ar");
                ffmpeg.addArgument(String.valueOf(samplingRate.intValue()));
            }
            Integer volume = audioAttributes.getVolume();
            if (volume != null) {
                ffmpeg.addArgument("-vol");
                ffmpeg.addArgument(String.valueOf(volume.intValue()));
            }
        }
        ffmpeg.addArgument("-f");
        ffmpeg.addArgument(formatAttribute);
        ffmpeg.addArgument("-y");
        ffmpeg.addArgument(target.getAbsolutePath());
        try {
            ffmpeg.execute();
        } catch (IOException e) {
            throw new EncoderException(e);
        } finally {
            ffmpeg.destroy();
        }
    }
}
