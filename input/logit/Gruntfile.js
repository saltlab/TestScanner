module.exports = function(grunt) {

  // Project configuration.
	grunt.initConfig({
		pkg: grunt.file.readJSON('package.json'),
	
		concat: {
			options: {
				// define a string to put between each file in the concatenated output
				separator: ';'
			},
			dist: {
				// the files to concatenate
				src: ['src/**/*.js'],
				// the location of the resulting JS file
				dest: 'dist/<%= pkg.name %>.js'
			}
		},
		
		uglify: {
			options: {
				banner: '/*! <%= pkg.name %> <%= grunt.template.today("yyyy-mm-dd") %> */\n',
				mangle: {
					except: ['jQuery', 'Backbone', 'qLog', '_']
				}
			},
			build: {
				files: {
					'dist/<%= pkg.name %>.min.js': ['<%= concat.dist.dest %>']
				}	
			}
		},
		
		qunit: {
			all: ['test/**/*.html']
		},
		
		jshint: {
			files : ['src/model/**/*.js', 'src/view/**/*.js', 'src/utils/**/*.js'],
		}
	});

	grunt.loadNpmTasks('grunt-contrib-uglify');
	grunt.loadNpmTasks('grunt-contrib-jshint');
	grunt.loadNpmTasks('grunt-contrib-qunit');
	grunt.loadNpmTasks('grunt-contrib-concat');

	//Tasks
	grunt.registerTask('test', ['qunit']);
	grunt.registerTask('hint', ['jshint']);
	grunt.registerTask('default', ['qunit', 'jshint', 'concat', 'uglify']);

};