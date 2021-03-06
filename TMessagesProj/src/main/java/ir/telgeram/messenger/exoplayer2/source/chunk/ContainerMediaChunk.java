/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package ir.telgeram.messenger.exoplayer2.source.chunk;

import ir.telgeram.messenger.exoplayer2.Format;
import ir.telgeram.messenger.exoplayer2.extractor.DefaultExtractorInput;
import ir.telgeram.messenger.exoplayer2.extractor.DefaultTrackOutput;
import ir.telgeram.messenger.exoplayer2.extractor.Extractor;
import ir.telgeram.messenger.exoplayer2.extractor.ExtractorInput;
import ir.telgeram.messenger.exoplayer2.extractor.SeekMap;
import ir.telgeram.messenger.exoplayer2.source.chunk.ChunkExtractorWrapper.SingleTrackMetadataOutput;
import ir.telgeram.messenger.exoplayer2.upstream.DataSource;
import ir.telgeram.messenger.exoplayer2.upstream.DataSpec;
import ir.telgeram.messenger.exoplayer2.util.Util;

import java.io.IOException;

/**
 * A {@link BaseMediaChunk} that uses an {@link Extractor} to decode sample data.
 */
public class ContainerMediaChunk extends BaseMediaChunk implements SingleTrackMetadataOutput {

  private final int chunkCount;
  private final long sampleOffsetUs;
  private final ChunkExtractorWrapper extractorWrapper;
  private final Format sampleFormat;

  private volatile int bytesLoaded;
  private volatile boolean loadCanceled;
  private volatile boolean loadCompleted;

  /**
   * @param dataSource The source from which the data should be loaded.
   * @param dataSpec Defines the data to be loaded.
   * @param trackFormat See {@link #trackFormat}.
   * @param trackSelectionReason See {@link #trackSelectionReason}.
   * @param trackSelectionData See {@link #trackSelectionData}.
   * @param startTimeUs The start time of the media contained by the chunk, in microseconds.
   * @param endTimeUs The end time of the media contained by the chunk, in microseconds.
   * @param chunkIndex The index of the chunk.
   * @param chunkCount The number of chunks in the underlying media that are spanned by this
   *     instance. Normally equal to one, but may be larger if multiple chunks as defined by the
   *     underlying media are being merged into a single load.
   * @param sampleOffsetUs An offset to add to the sample timestamps parsed by the extractor.
   * @param extractorWrapper A wrapped extractor to use for parsing the data.
   * @param sampleFormat The {@link Format} of the samples in the chunk, if known. May be null if
   *     the data is known to define its own sample format.
   */
  public ContainerMediaChunk(DataSource dataSource, DataSpec dataSpec, Format trackFormat,
      int trackSelectionReason, Object trackSelectionData, long startTimeUs, long endTimeUs,
      int chunkIndex, int chunkCount, long sampleOffsetUs, ChunkExtractorWrapper extractorWrapper,
      Format sampleFormat) {
    super(dataSource, dataSpec, trackFormat, trackSelectionReason, trackSelectionData, startTimeUs,
        endTimeUs, chunkIndex);
    this.chunkCount = chunkCount;
    this.sampleOffsetUs = sampleOffsetUs;
    this.extractorWrapper = extractorWrapper;
    this.sampleFormat = sampleFormat;
  }

  @Override
  public int getNextChunkIndex() {
    return chunkIndex + chunkCount;
  }

  @Override
  public boolean isLoadCompleted() {
    return loadCompleted;
  }

  @Override
  public final long bytesLoaded() {
    return bytesLoaded;
  }

  // SingleTrackMetadataOutput implementation.

  @Override
  public final void seekMap(SeekMap seekMap) {
    // Do nothing.
  }

  // Loadable implementation.

  @Override
  public final void cancelLoad() {
    loadCanceled = true;
  }

  @Override
  public final boolean isLoadCanceled() {
    return loadCanceled;
  }

  @SuppressWarnings("NonAtomicVolatileUpdate")
  @Override
  public final void load() throws IOException, InterruptedException {
    DataSpec loadDataSpec = Util.getRemainderDataSpec(dataSpec, bytesLoaded);
    try {
      // Create and open the input.
      ExtractorInput input = new DefaultExtractorInput(dataSource,
          loadDataSpec.absoluteStreamPosition, dataSource.open(loadDataSpec));
      if (bytesLoaded == 0) {
        // Set the target to ourselves.
        DefaultTrackOutput trackOutput = getTrackOutput();
        trackOutput.formatWithOffset(sampleFormat, sampleOffsetUs);
        extractorWrapper.init(this, trackOutput);
      }
      // Load and decode the sample data.
      try {
        int result = Extractor.RESULT_CONTINUE;
        while (result == Extractor.RESULT_CONTINUE && !loadCanceled) {
          result = extractorWrapper.read(input);
        }
      } finally {
        bytesLoaded = (int) (input.getPosition() - dataSpec.absoluteStreamPosition);
      }
    } finally {
      dataSource.close();
    }
    loadCompleted = true;
  }

}
