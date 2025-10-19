CREATE TABLE authors (
	id INTEGER NOT NULL, 
	first_name VARCHAR(128), 
	last_name VARCHAR(128), 
	display_name VARCHAR(255) NOT NULL, 
	PRIMARY KEY (id)
);
CREATE TABLE song_books (
	id INTEGER NOT NULL, 
	name VARCHAR(128) NOT NULL, 
	publisher VARCHAR(128), 
	PRIMARY KEY (id)
);
CREATE TABLE songs (
	id INTEGER NOT NULL, 
	title VARCHAR(255) NOT NULL, 
	alternate_title VARCHAR(255), 
	lyrics TEXT NOT NULL, 
	verse_order VARCHAR(128), 
	copyright VARCHAR(255), 
	comments TEXT, 
	ccli_number VARCHAR(64), 
	theme_name VARCHAR(128), 
	search_title VARCHAR(255) NOT NULL, 
	search_lyrics TEXT NOT NULL, 
	create_date DATETIME, 
	last_modified DATETIME, 
	"temporary" BOOLEAN, 
	PRIMARY KEY (id)
);
CREATE TABLE topics (
	id INTEGER NOT NULL, 
	name VARCHAR(128) NOT NULL, 
	PRIMARY KEY (id)
);
CREATE TABLE media_files (
	id INTEGER NOT NULL, 
	song_id INTEGER, 
	file_path VARCHAR NOT NULL, 
	file_hash VARCHAR(128) NOT NULL, 
	type VARCHAR(64) NOT NULL, 
	weight INTEGER, 
	PRIMARY KEY (id), 
	FOREIGN KEY(song_id) REFERENCES songs (id)
);
CREATE TABLE authors_songs (
	author_id INTEGER NOT NULL, 
	song_id INTEGER NOT NULL, 
	author_type VARCHAR(255) DEFAULT "" NOT NULL, 
	PRIMARY KEY (author_id, song_id, author_type), 
	FOREIGN KEY(author_id) REFERENCES authors (id), 
	FOREIGN KEY(song_id) REFERENCES songs (id)
);
CREATE TABLE songs_songbooks (
	songbook_id INTEGER NOT NULL, 
	song_id INTEGER NOT NULL, 
	entry VARCHAR(255) NOT NULL, 
	PRIMARY KEY (songbook_id, song_id, entry), 
	FOREIGN KEY(songbook_id) REFERENCES song_books (id),
	FOREIGN KEY(song_id) REFERENCES songs (id)
);
CREATE TABLE songs_topics (
	song_id INTEGER NOT NULL, 
	topic_id INTEGER NOT NULL, 
	PRIMARY KEY (song_id, topic_id), 
	FOREIGN KEY(song_id) REFERENCES songs (id), 
	FOREIGN KEY(topic_id) REFERENCES topics (id)
);
CREATE TABLE metadata (
	"key" VARCHAR(64) NOT NULL, 
	value TEXT, 
	PRIMARY KEY ("key")
);
INSERT INTO metadata VALUES('version','8');
CREATE INDEX ix_authors_display_name ON authors (display_name);
CREATE INDEX ix_songs_search_title ON songs (search_title);
CREATE INDEX ix_topics_name ON topics (name)
