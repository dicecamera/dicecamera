package com.sorasoft.dicecam.view.album;

import java.util.ArrayList;

public interface DataSource {
	
	void loadMediaSets(final MediaFeed feed);

	void loadItemsForSet(final MediaFeed feed, final MediaSet parentSet, int rangeStart, int rangeEnd);

	void shutdown();

	boolean performOperation(int operation, ArrayList<MediaBucket> mediaBuckets, Object data);

}
