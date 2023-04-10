#include "../../../../oboe/include/oboe/AudioStreamCallback.h"
#include "../../../../oboe/include/oboe/Definitions.h"

class DefaultAudioStreamDataCallback : public oboe::AudioStreamDataCallback {

    oboe::DataCallbackResult
    onAudioReady(oboe::AudioStream *audioStream, void *audioData, int32_t numFrames) override {
        return oboe::DataCallbackResult::Continue;
    }
};